package com.pagamento.boleto.domain.exception;

public class GatewayIntegrationException extends RuntimeException {

    public GatewayIntegrationException(String message) {
        super(message);
    }

    public GatewayIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
