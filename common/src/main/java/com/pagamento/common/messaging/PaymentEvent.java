package com.pagamento.common.messaging;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentEvent(
    String transactionId,  // Nome alterado para manter consistência
    String paymentType,
    BigDecimal amount,
    Instant timestamp,
    String status
) {
    // Construtor compacto para validações
    public PaymentEvent {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID não pode ser nulo ou vazio");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        if (status == null || status.isBlank()) {
            status = "PENDENTE"; // Valor padrão mais adequado
        }
    }
    
    // Construtor alternativo com status padrão
    public PaymentEvent(String transactionId, String paymentType, BigDecimal amount, Instant timestamp) {
        this(transactionId, paymentType, amount, timestamp, "PENDENTE");
    }
}