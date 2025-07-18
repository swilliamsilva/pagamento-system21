package com.pagamento.pix.infrastructure.integration;

public class BacenIntegrationException extends RuntimeException {
    public BacenIntegrationException(String message) {
        super(message);
    }
    
    public BacenIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}