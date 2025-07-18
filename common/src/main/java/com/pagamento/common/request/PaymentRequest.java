package com.pagamento.common.request;

import java.math.BigDecimal;

/**
 * Dados de entrada para iniciar um pagamento.
 */
public record PaymentRequest(
    String userId,
    String tipoPagamento,  // Mantido o nome original do campo
    BigDecimal valor
) {}