package com.pagamento.common.messaging;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentEvent(
    String paymentId,
    String paymentType,
    BigDecimal amount,
    Instant timestamp,
    String status
) {
    // Construtor compacto para validações/adicionar lógica
    public PaymentEvent {
        if (status == null || status.isBlank()) {
            status = "PROCESSADO"; // Valor padrão
        }
    }
    
    // Construtor alternativo
    public PaymentEvent(String paymentId, String paymentType, BigDecimal amount, Instant timestamp) {
        this(paymentId, paymentType, amount, timestamp, "PROCESSADO");
    }

	public Object getTransactionId() {
		// TODO Auto-generated method stub
		return null;
	}
}