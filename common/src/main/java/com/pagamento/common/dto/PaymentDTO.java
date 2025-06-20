package com.pagamento.common.dto;

import java.math.BigDecimal;

public record PaymentDTO(
    String idTransacao,
    String tipoPagamento, // PIX, BOLETO, CARTAO
    BigDecimal valor
) {}
