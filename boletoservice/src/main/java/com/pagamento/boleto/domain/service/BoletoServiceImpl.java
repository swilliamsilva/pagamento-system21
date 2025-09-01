package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.exception.BoletoNotFoundException;
import com.pagamento.boleto.domain.exception.DadosTecnicosNaoDisponiveisException;
import com.pagamento.boleto.domain.exception.GatewayIntegrationException;
import com.pagamento.boleto.domain.exception.NotificationException;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.in.BoletoServicePort;
import com.pagamento.boleto.domain.ports.out.AsaasGatewayPort;
import com.pagamento.boleto.domain.ports.out.BoletoRepositoryPort;
import com.pagamento.boleto.domain.ports.out.NotificacaoPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BoletoServiceImpl implements BoletoServicePort {

    private static final Logger logger = LoggerFactory.getLogger(BoletoServiceImpl.class);

    private final BoletoRepositoryPort repository;
    private final AsaasGatewayPort asaasGateway;
    private final NotificacaoPort notificacaoPort;
    private final BoletoValidation validation;
    private final BoletoFactory factory;
    private final TaxasService taxasService;
    private final PdfService pdfService;

    public BoletoServiceImpl(
            BoletoRepositoryPort repository,
            AsaasGatewayPort asaasGateway,
            NotificacaoPort notificacaoPort,
            BoletoValidation validation,
            BoletoFactory factory,
            TaxasService taxasService,
            PdfService pdfService) {
        this.repository = repository;
        this.asaasGateway = asaasGateway;
        this.notificacaoPort = notificacaoPort;
        this.validation = validation;
        this.factory = factory;
        this.taxasService = taxasService;
        this.pdfService = pdfService;
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
    public Page<Boleto> listarBoletos(Pageable pageable) {
        return repository.listarTodos(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] gerarPDF(String id) {
        Boleto boleto = consultarBoleto(id);
        return pdfService.gerarPdf(boleto);
    }

    @Override
    @Transactional(readOnly = true)
    public String gerarCodigoBarras(String id) {
        Boleto boleto = consultarBoleto(id);
        if (boleto.getDadosTecnicos() == null) {
            throw new DadosTecnicosNaoDisponiveisException("Dados técnicos não disponíveis para o boleto: " + id);
        }
        return boleto.getDadosTecnicos().codigoBarras();
    }

    @Override
    @Transactional(readOnly = true)
    public String gerarQRCode(String id) {
        Boleto boleto = consultarBoleto(id);
        if (boleto.getDadosTecnicos() == null) {
            throw new DadosTecnicosNaoDisponiveisException("Dados técnicos não disponíveis para o boleto: " + id);
        }
        return boleto.getDadosTecnicos().qrCode();
    }

    @Override
    public UUID gerarBoleto(com.pagamento.common.dto.BoletoRequestDTO request) {
        BoletoRequestDTO dto = convertToLocalDTO(request);
        Boleto boleto = emitirBoleto(dto);
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
}


