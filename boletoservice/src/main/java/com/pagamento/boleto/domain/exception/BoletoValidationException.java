package com.pagamento.boleto.domain.exception;

public class BoletoValidationException extends RuntimeException {
    public BoletoValidationException(String message) {
        super(message);
    }
}