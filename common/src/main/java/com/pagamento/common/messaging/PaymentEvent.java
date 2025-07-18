package com.pagamento.common.messaging;

public record PaymentEvent(String transactionId, String status) {}