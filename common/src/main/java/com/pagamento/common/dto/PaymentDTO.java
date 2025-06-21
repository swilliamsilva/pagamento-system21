package com.pagamento.common.dto;
/***
 * Orquestrador do pagamento
 * 
 */


import java.math.BigDecimal;

/**
 * Representa dados públicos e imutáveis de um pagamento.
 */
public record PaymentDTO(
    String idTransacao,
    String tipoPagamento, // PIX, BOLETO, CARTAO
    BigDecimal valor
) {}
