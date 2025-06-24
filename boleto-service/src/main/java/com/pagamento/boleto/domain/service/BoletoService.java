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
public class BoletoService {

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

    public void processarPagamento(String idBoleto, LocalDate dataPagamento) {
        Boleto boleto = repository.buscarPorId(idBoleto)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto não encontrado"));

        validation.validarPagamento(boleto);
        boleto.marcarComoPago(dataPagamento);
        repository.atualizar(boleto);

        try {
            notificacaoPort.notificarPagamento(boleto);
        } catch (NotificationException e) {
            logger.error("Falha ao notificar pagamento: {}", e.getMessage());
        }

        try {
            asaasGateway.confirmarPagamento(boleto.getIdExterno(), dataPagamento);
        } catch (GatewayIntegrationException e) {
            logger.error("Falha ao confirmar pagamento no gateway: {}", e.getMessage());
        }
    }

    public void cancelarBoleto(String idBoleto, String motivo) {
        Boleto boleto = repository.buscarPorId(idBoleto)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto não encontrado"));

        validation.validarCancelamento(boleto);
        boleto.cancelar(motivo);
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
    }

    public Boleto consultarBoleto(String id) {
        return repository.buscarPorId(id)
            .orElseThrow(() -> new BoletoNotFoundException("Boleto não encontrado"));
    }

    public byte[] gerarPDF(String id) {
        Boleto boleto = consultarBoleto(id);
        return calculos.gerarPDF(boleto);
    }

    public String gerarCodigoBarras(String id) {
        Boleto boleto = consultarBoleto(id);
        return calculos.gerarCodigoBarras(boleto);
    }

    public String gerarQRCode(String id) {
        Boleto boleto = consultarBoleto(id);
        return calculos.gerarQRCode(boleto);
    }
}
