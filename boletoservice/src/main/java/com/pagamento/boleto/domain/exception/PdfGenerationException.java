package com.pagamento.boleto.domain.exception;
public class PdfGenerationException extends RuntimeException {
    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}