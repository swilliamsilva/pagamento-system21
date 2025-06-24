package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.domain.ports.BoletoServicePort;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoFactory;
import com.pagamento.boleto.domain.model.BoletoStatus;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class BoletoServiceImpl implements BoletoServicePort {

    private final BoletoRepositoryPort repository;
    private final BoletoFactory factory;
    private final BoletoCalculos calculos;
    private final BoletoValidation validation;

    public BoletoServiceImpl(
        BoletoRepositoryPort repository,
        BoletoFactory factory,
        BoletoCalculos calculos,
        BoletoValidation validation
    ) {
        this.repository = repository;
        this.factory = factory;
        this.calculos = calculos;
        this.validation = validation;
    }

    @Override
    public Boleto emitirBoleto(BoletoRequestDTO dto) {
        validation.validarEmissao(dto);
        Boleto boleto = factory.criarBoleto(dto);
        boleto.setStatus(BoletoStatus.EMITIDO);
        return repository.salvar(boleto);
    }

    @Override
    public Boleto cancelarBoleto(String id) {
        Boleto boleto = repository.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Boleto não encontrado"));
        validation.validarCancelamento(boleto);
        boleto.setStatus(BoletoStatus.CANCELADO);
        return repository.salvar(boleto);
    }

    @Override
    public Boleto consultarBoleto(String id) {
        return repository.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Boleto não encontrado"));
    }

    @Override
    public Boleto reemitirBoleto(String id) {
        Boleto original = repository.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Boleto original não encontrado"));
        validation.validarReemissao(original);
        Boleto reemissao = factory.criarReemissao(original);
        calculos.aplicarTaxasReemissao(reemissao);
        return repository.salvar(reemissao);
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

    public BoletoCalculos getCalculos() {
        return calculos;
    }
}
