package com.pagamento.common.resilience;

/**
 * Exceção lançada quando uma operação é rejeitada devido ao circuit breaker estar aberto.
 * Indica que as chamadas estão temporariamente suspensas devido a falhas consecutivas.
 */
public class CircuitBreakerOpenException extends RuntimeException {
    private final String circuitBreakerName;

    public CircuitBreakerOpenException(String circuitBreakerName) {
        super("Circuit breaker '" + circuitBreakerName + "' está aberto e bloqueando chamadas");
        this.circuitBreakerName = circuitBreakerName;
    }

    public CircuitBreakerOpenException(String circuitBreakerName, Throwable cause) {
        super("Circuit breaker '" + circuitBreakerName + "' aberto | Causa: " + cause.getMessage(), cause);
        this.circuitBreakerName = circuitBreakerName;
    }

    public String getCircuitBreakerName() {
        return circuitBreakerName;
    }

    @Override
    public String toString() {
        return "CircuitBreakerOpenException{" +
               "circuitBreakerName='" + circuitBreakerName + '\'' +
               ", message='" + getMessage() + '\'' +
               '}';
    }
}