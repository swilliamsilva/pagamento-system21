/* ============================
 * UNIFICADO: BoletoService.java
 * ============================ */
package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.exception.*;
import com.pagamento.boleto.domain.model.*;
import com.pagamento.boleto.domain.ports.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BoletoService implements BoletoServicePort {

    private static final Logger logger = LoggerFactory.getLogger(BoletoService.class);

    private final BoletoRepositoryPort repository;
    private final AsaasGatewayPort asaasGateway;
    private final NotificacaoPort notificacaoPort;
    private final BoletoValidation validation;
    private final BoletoCalculos calculos;
    private final BoletoFactory factory;

    public BoletoService(
        BoletoRepositoryPort repository,
        AsaasGatewayPort asaasGateway,
        NotificacaoPort notificacaoPort,
        BoletoValidation validation,
        BoletoCalculos calculos,
        BoletoFactory factory
    ) {
        this.repository = repository;
        this.asaasGateway = asaasGateway;
        this.notificacaoPort = notificacaoPort;
        this.validation = validation;
        this.calculos = calculos;
        this.factory = factory;
    }

    @Override
    public Boleto emitirBoleto(BoletoRequestDTO dto) {
        Boleto boleto = null;
        try {
            validation.validarEmissao(dto);
            boleto = factory.criarBoleto(dto);
            calculos.aplicarTaxas(boleto);
            validation.validarBoleto(boleto);
            boleto = repository.salvar(boleto);

            String idExterno = asaasGateway.registrarBoleto(boleto);
            boleto.setIdExterno(idExterno);
            boleto = repository.atualizar(boleto);

            notificacaoPort.notificarEmissao(boleto);
            return boleto;
        } catch (BoletoValidationException e) {
            throw new BusinessException("Erro de validação: " + e.getMessage(), e);
        } catch (GatewayIntegrationException e) {
            if (boleto != null && boleto.getId() != null) {
                repository.deletarPorId(boleto.getId());
            }
            throw new GatewayIntegrationException("Falha na integração com gateway: " + e.getMessage(), e);
        } catch (NotificationException e) {
            logger.error("Falha ao enviar notificação: {}", e.getMessage());
            if (boleto != null) return boleto;
            throw new BusinessException("Falha na emissão do boleto", e);
        }
    }

    @Override
    public Boleto reemitirBoleto(String idOriginal) {
        Boleto original = repository.buscarPorId(idOriginal)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto original não encontrado"));

        validation.validarReemissao(original);
        Boleto reemissao = factory.criarReemissao(original);
        calculos.aplicarTaxasReemissao(reemissao);
        reemissao = repository.salvar(reemissao);

        String idExterno = asaasGateway.registrarBoleto(reemissao);
        reemissao.setIdExterno(idExterno);
        reemissao = repository.atualizar(reemissao);

        original.incrementarReemissoes();
        repository.atualizar(original);

        try {
            notificacaoPort.notificarReemissao(original, reemissao);
        } catch (NotificationException e) {
            logger.error("Falha ao notificar reemissão: {}", e.getMessage());
        }

        return reemissao;
    }

    @Override
    public Boleto cancelarBoleto(String id) {
        Boleto boleto = repository.buscarPorId(id)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto não encontrado"));

        validation.validarCancelamento(boleto);
        boleto.setStatus(BoletoStatus.CANCELADO);
        repository.atualizar(boleto);

        try {
            asaasGateway.cancelarBoleto(boleto.getIdExterno());
        } catch (GatewayIntegrationException e) {
            logger.error("Falha ao cancelar no gateway: {}", e.getMessage());
        }

        try {
            notificacaoPort.notificarCancelamento(boleto);
        } catch (NotificationException e) {
            logger.error("Falha ao notificar cancelamento: {}", e.getMessage());
        }

        return boleto;
    }

    @Override
    public Boleto consultarBoleto(String id) {
        return repository.buscarPorId(id)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto não encontrado"));
    }

    @Override
    public byte[] gerarPDF(String id) {
        Boleto boleto = consultarBoleto(id);
        return calculos.gerarPDF(boleto);
    }

    @Override
    public String gerarCodigoBarras(String id) {
        Boleto boleto = consultarBoleto(id);
        return calculos.gerarCodigoBarras(boleto);
    }

    @Override
    public String gerarQRCode(String id) {
        Boleto boleto = consultarBoleto(id);
        return calculos.gerarQRCode(boleto);
    }

	public void cancelarBoleto(String id, String motivo) {
		// TODO Auto-generated method stub
		
	}

	public String gerarBoleto(com.pagamento.common.dto.BoletoRequestDTO request) {
		// TODO Auto-generated method stub
		return null;
	}
} 
