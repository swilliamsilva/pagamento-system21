package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.*;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.exception.BoletoValidationException;

import java.time.LocalDate;
import java.util.Optional;
import java.math.BigDecimal;

public class BoletoValidation {

    public void validarEmissao(BoletoRequestDTO dto) {
        if (dto.valor() == null || dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            /*
             * 
             * The method compareTo(Double) in the type Double is not applicable for the arguments (BigDecimal)
             * 
             * ***/
        	
        	
        	throw new BoletoValidationException("Valor deve ser positivo");
        }
        
        if (dto.dataVencimento() == null || dto.dataVencimento().isBefore(LocalDate.now())) {
            throw new BoletoValidationException("Data de vencimento deve ser futura");
        }
        
        if (dto.pagador() == null || dto.pagador().isBlank()) {
            throw new BoletoValidationException("Pagador é obrigatório");
        }
        
        if (dto.beneficiario() == null || dto.beneficiario().isBlank()) {
            throw new BoletoValidationException("Beneficiário é obrigatório");
        }
    }

    public void validarBoleto(Boleto boleto) {
        if (boleto.getDataVencimento() == null) {
            throw new BoletoValidationException("Data de vencimento não definida");
        }
        
        if (boleto.getStatus() != BoletoStatus.EMITIDO) {
            throw new BoletoValidationException("Boleto não está em estado válido para operação");
        }
    }

    public void validarReemissao(Boleto original) {
        if (original.getStatus() != BoletoStatus.VENCIDO && 
            original.getStatus() != BoletoStatus.EMITIDO) {
            throw new BoletoValidationException("Boleto não pode ser reemitido. Status atual: " + original.getStatus());
        }
        
        if (original.getNumeroReemissoes() >= 3) {
            throw new BoletoValidationException("Número máximo de reemissoes atingido");
        }
    }

    public void validarCancelamento(Boleto boleto) {
        if (boleto.getStatus() == BoletoStatus.PAGO) {
            throw new BoletoValidationException("Boleto já pago não pode ser cancelado");
        }
        
        if (boleto.getStatus() == BoletoStatus.CANCELADO) {
            throw new BoletoValidationException("Boleto já está cancelado");
        }
    }
    
    public void validarPagamento(Boleto boleto) {
        if (boleto.getStatus() != BoletoStatus.EMITIDO && 
            boleto.getStatus() != BoletoStatus.REEMITIDO &&
            boleto.getStatus() != BoletoStatus.VENCIDO) {
            throw new BoletoValidationException("Boleto não pode ser pago. Status atual: " + boleto.getStatus());
        }
        
        if (boleto.getDataVencimento().isBefore(LocalDate.now())) {
            throw new BoletoValidationException("Boleto vencido requer atualização de status");
        }
    }

	public void validarCancelamento(Optional<Boleto> boleto) {
		// TODO Auto-generated method stub
		
	}
}