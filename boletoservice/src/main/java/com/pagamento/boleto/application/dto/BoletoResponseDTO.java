package com.pagamento.boleto.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BoletoResponseDTO(
    String id,
    String pagador,
    String beneficiario,
    BigDecimal valor,
    LocalDate dataEmissao,
    LocalDate dataVencimento,
    String status,
    String codigoBarras,
    String linhaDigitavel,
    String qrCode,
    String documento,
    String instrucoes,
    String localPagamento,
    String boletoOriginalId,
    int numeroReemissoes
) {

	public Object getId() {
		// TODO Auto-generated method stub
		return null;
	}}