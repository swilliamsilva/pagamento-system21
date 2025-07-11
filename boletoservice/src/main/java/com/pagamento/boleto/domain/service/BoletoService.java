package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.exception.*;
import com.pagamento.boleto.domain.model.*;
import com.pagamento.boleto.domain.ports.*;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public class BoletoService implements BoletoServicePort {

    private static final Logger logger = LoggerFactory.getLogger(BoletoService.class);

    private final BoletoRepositoryPort repository;
    private final AsaasGatewayPort asaasGateway;
    private final NotificacaoPort notificacaoPort;
    private final BoletoValidation validation;
    private final BoletoFactory factory;
    private final TaxasService taxasService;
    private final PdfService pdfService;
    private final ApplicationContext applicationContext;

    public BoletoService(
        BoletoRepositoryPort repository,
        AsaasGatewayPort asaasGateway,
        NotificacaoPort notificacaoPort,
        BoletoValidation validation,
        BoletoFactory factory,
        TaxasService taxasService,
        PdfService pdfService,
        ApplicationContext applicationContext
    ) {
        this.repository = repository;
        this.asaasGateway = asaasGateway;
        this.notificacaoPort = notificacaoPort;
        this.validation = validation;
        this.factory = factory;
        this.taxasService = taxasService;
        this.pdfService = pdfService;
        this.applicationContext = applicationContext;
    }

    // Método auxiliar para obter proxy transacional
    private BoletoServicePort getTransactionalService() {
        return applicationContext.getBean(BoletoServicePort.class);
    }

    @Override
    @Transactional
    public Boleto emitirBoleto(BoletoRequestDTO dto) {
        validation.validarEmissao(dto);
        Boleto boleto = factory.criarBoleto(dto);
        taxasService.aplicarTaxasEmissao(boleto);
        validation.validarBoleto(boleto);
        
        boleto = repository.salvar(boleto);
        
        try {
            String idExterno = asaasGateway.registrarBoleto(boleto);
            boleto.setIdExterno(idExterno);
            boleto.adicionarStatus(BoletoStatus.EMITIDO);
            boleto = repository.atualizar(boleto);
            
            notificacaoPort.notificarEmissao(boleto);
            return boleto;
        } catch (GatewayIntegrationException e) {
            repository.deletar(boleto.getId());
            throw new GatewayIntegrationException("Falha no registro no gateway de pagamento", e);
        } catch (NotificationException e) {
            logger.error("Falha na notificação de emissão do boleto {}", boleto.getId(), e);
            return boleto;
        }
    }

    @Override
    @Transactional
    public Boleto reemitirBoleto(String idOriginal) {
        Boleto original = repository.buscarPorId(idOriginal)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto original não encontrado: " + idOriginal));

        validation.validarReemissao(original);
        Boleto reemissao = factory.criarReemissao(original, 30);
        taxasService.aplicarTaxasReemissao(reemissao);
        
        reemissao = repository.salvar(reemissao);
        
        try {
            String idExterno = asaasGateway.registrarBoleto(reemissao);
            reemissao.setIdExterno(idExterno);
            reemissao.adicionarStatus(BoletoStatus.REEMITIDO);
            reemissao = repository.atualizar(reemissao);
            
            original.incrementarReemissoes();
            original.adicionarStatus(BoletoStatus.REEMITIDO);
            repository.atualizar(original);
            
            notificacaoPort.notificarReemissao(original, reemissao);
            return reemissao;
        } catch (GatewayIntegrationException e) {
            repository.deletar(reemissao.getId());
            throw new GatewayIntegrationException("Falha no registro da reemissão no gateway", e);
        } catch (NotificationException e) {
            logger.error("Falha ao notificar reemissão do boleto {}", reemissao.getId(), e);
            return reemissao;
        }
    }

    @Override
    @Transactional
    public Boleto cancelarBoleto(String id, String motivo) {
        Boleto boleto = repository.buscarPorId(id)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto não encontrado: " + id));

        validation.validarCancelamento(boleto);
        boleto.cancelar(motivo);
        boleto = repository.atualizar(boleto);
        
        try {
            if (boleto.getIdExterno() != null) {
                asaasGateway.cancelarBoleto(boleto.getIdExterno());
            }
        } catch (GatewayIntegrationException e) {
            logger.error("Falha ao cancelar boleto {} no gateway", boleto.getIdExterno(), e);
        }

        try {
            notificacaoPort.notificarCancelamento(boleto);
        } catch (NotificationException e) {
            logger.error("Falha ao notificar cancelamento do boleto {}", boleto.getId(), e);
        }

        return boleto;
    }

    @Override
    @Transactional(readOnly = true)
    public Boleto consultarBoleto(String id) {
        return repository.buscarPorId(id)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto não encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] gerarPDF(String id) {
        Boleto boleto = getTransactionalService().consultarBoleto(id);
        return pdfService.gerarPdf(boleto);
    }

    @Override
    @Transactional(readOnly = true)
    public String gerarCodigoBarras(String id) {
        Boleto boleto = getTransactionalService().consultarBoleto(id);
        if (boleto.getDadosTecnicos() == null) {
            throw new DadosTecnicosNaoDisponiveisException("Dados técnicos não disponíveis para o boleto: " + id);
        }
        return boleto.getDadosTecnicos().codigoBarras();
    }

    @Override
    @Transactional(readOnly = true)
    public String gerarQRCode(String id) {
        Boleto boleto = getTransactionalService().consultarBoleto(id);
        if (boleto.getDadosTecnicos() == null) {
            throw new DadosTecnicosNaoDisponiveisException("Dados técnicos não disponíveis para o boleto: " + id);
        }
        return boleto.getDadosTecnicos().qrCode();
    }

    /**
     * @return 
     * @deprecated Este método foi substituído por {@link #cancelarBoleto(String, String)}.
     *             Será removido em versões futuras.
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @Override
    public Boleto cancelarBoleto(String id) {
        getTransactionalService().cancelarBoleto(id, "Cancelamento solicitado");
    }

    @Override
    public UUID gerarBoleto(com.pagamento.common.dto.BoletoRequestDTO request) {
        BoletoRequestDTO dto = convertToLocalDTO(request);
        Boleto boleto = getTransactionalService().emitirBoleto(dto);
        return boleto.getId();
    }
    
    private BoletoRequestDTO convertToLocalDTO(com.pagamento.common.dto.BoletoRequestDTO request) {
        return new BoletoRequestDTO(
            request.getPagador(),
            request.getBeneficiario(),
            request.getValor(),
            request.getDataVencimento(),
            request.getDocumento(),
            request.getInstrucoes(),
            request.getLocalPagamento()
        );
    }

	@Override
	public Object listarBoletos(Pageable any) {
		// TODO Auto-generated method stub
		return null;
	}
}