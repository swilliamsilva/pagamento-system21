package com.pagamento.common.messaging;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentEvent(
    String paymentId,
    String paymentType,
    BigDecimal amount,
    Instant timestamp
) {}