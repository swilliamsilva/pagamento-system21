package com.pagamento.pix.infrastructure.integration;

public class PaymentFailedException extends Exception {
    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}