package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.exception.BoletoValidationException;

import java.time.LocalDate;
import java.math.BigDecimal;

public class BoletoValidation {

    public void validarEmissao(BoletoRequestDTO dto) {
        // Validação de valor convertido para BigDecimal
        BigDecimal valor = BigDecimal.valueOf(dto.valor());
        if (dto.valor() == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BoletoValidationException("Valor deve ser positivo");
        }
        
        if (dto.dataVencimento() == null || dto.dataVencimento().isBefore(LocalDate.now().plusDays(1))) {
            throw new BoletoValidationException("Data de vencimento deve ser futura (mínimo 1 dia)");
        }
        
        if (dto.pagador() == null || dto.pagador().isBlank()) {
            throw new BoletoValidationException("Pagador é obrigatório");
        }
        
        if (dto.beneficiario() == null || dto.beneficiario().isBlank()) {
            throw new BoletoValidationException("Beneficiário é obrigatório");
        }
    }

    public void validarBoleto(Boleto boleto) {
        if (boleto == null) {
            throw new BoletoValidationException("Boleto não pode ser nulo");
        }
        
        if (boleto.getDataVencimento() == null) {
            throw new BoletoValidationException("Data de vencimento não definida");
        }
        
        if (boleto.getStatus() == null) {
            throw new BoletoValidationException("Status não definido");
        }
        
        if (!boleto.getStatus().isOperacaoPermitida()) {
            throw new BoletoValidationException(
                "Boleto não está em estado válido para operação: " + boleto.getStatus()
            );
        }
    }

    public void validarReemissao(Boleto original) {
        validarBoleto(original);
        
        if (!original.getStatus().permiteReemissao()) {
            throw new BoletoValidationException(
                "Boleto não pode ser reemitido. Status atual: " + original.getStatus()
            );
        }
        
        if (original.getReemissoes() >= 3) {
            throw new BoletoValidationException("Número máximo de reemissoes (3) atingido");
        }
    }

    public void validarCancelamento(Boleto boleto) {
        validarBoleto(boleto);
        
        if (boleto.getStatus() == BoletoStatus.PAGO) {
            throw new BoletoValidationException("Boleto já pago não pode ser cancelado");
        }
        
        if (boleto.getStatus() == BoletoStatus.CANCELADO) {
            throw new BoletoValidationException("Boleto já está cancelado");
        }
        
        if (boleto.getStatus() == BoletoStatus.VENCIDO) {
            throw new BoletoValidationException("Boleto vencido não pode ser cancelado");
        }
    }
    
    public void validarPagamento(Boleto boleto, BigDecimal valorPago) {
        validarBoleto(boleto);
        
        if (!boleto.getStatus().permitePagamento()) {
            throw new BoletoValidationException(
                "Boleto não pode ser pago. Status atual: " + boleto.getStatus()
            );
        }
        
        if (valorPago.compareTo(boleto.getValor()) < 0) {
            throw new BoletoValidationException(
                "Valor pago insuficiente. Esperado: " + boleto.getValor() + ", Recebido: " + valorPago
            );
        }
    }
}