package com.pagamento.common.request;


/***
 * Orquestrador do pagamento
 * 
 */



import java.math.BigDecimal;

/**
 * Dados de entrada para iniciar um pagamento.
 */
public record PaymentRequest(
    String userId,
    String tipoPagamento,
    BigDecimal valor
) {}
