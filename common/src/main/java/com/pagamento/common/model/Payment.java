package com.pagamento.common.model;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class Payment {
    private final String transactionId;
    private final String userId;
    private final String paymentType;
    private final BigDecimal amount;
    private final Instant createdAt;
    private String status; // Pode ser alterado durante o processamento
}