/* ========================================================
# Classe: BoletoRequestDTO
# Módulo: common
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: DTO de requisição para emissão de boleto.
# ======================================================== */

package com.pagamento.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BoletoRequestDTO(
    String pagador,
    String beneficiario,
    BigDecimal valor,
    LocalDate dataVencimento,
    String documento,
    String instrucoes,
    String localPagamento
) {

	public @NotBlank(message = "Pagador é obrigatório") String getPagador() {
		// TODO Auto-generated method stub
		return null;
	}
    // Construtor padrão do record é gerado automaticamente

	public @NotBlank(message = "Beneficiário é obrigatório") String getBeneficiario() {
		// TODO Auto-generated method stub
		return null;
	}

	public @NotNull(message = "Valor é obrigatório") @Positive(message = "Valor deve ser positivo") BigDecimal getValor() {
		// TODO Auto-generated method stub
		return null;
	}

	public @NotNull(message = "Data de vencimento é obrigatória") @Future(message = "Data de vencimento deve ser futura") LocalDate getDataVencimento() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDocumento() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInstrucoes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalPagamento() {
		// TODO Auto-generated method stub
		return null;
	}
}