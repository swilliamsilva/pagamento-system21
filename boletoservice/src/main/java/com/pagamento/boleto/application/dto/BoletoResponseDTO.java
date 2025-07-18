package com.pagamento.boleto.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BoletoResponseDTO(
    String id,
    String pagador,
    String beneficiario,
    BigDecimal valor,
    LocalDate dataVencimento,
    LocalDate dataEmissao,
    String documento,
    String instrucoes,
    String localPagamento,
    String status,
    String motivoCancelamento,
    int numeroReemissoes,
    String boletoOriginalId
) {}