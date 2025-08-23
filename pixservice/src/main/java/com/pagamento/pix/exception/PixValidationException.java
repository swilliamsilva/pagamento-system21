package com.pagamento.pix.exception;

public class PixValidationException extends RuntimeException {
    public PixValidationException(String message) {
        super(message);
    }

    public PixValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}


