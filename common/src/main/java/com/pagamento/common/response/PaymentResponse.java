package com.pagamento.common.response;


/***
 * Orquestrador do pagamento
 * 
 */



import java.math.BigDecimal;

/**
 * Resposta da API após processar o pagamento.
 */
public record PaymentResponse(
    String idTransacao,
    String status,
    BigDecimal valor,
    String tipoPagamento
) {}
