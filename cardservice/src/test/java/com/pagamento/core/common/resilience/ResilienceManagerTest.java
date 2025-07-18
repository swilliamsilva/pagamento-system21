package com.pagamento.core.common.resilience;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResilienceManagerTest {

    @Test
    void shouldOpenCircuitAfterFailures() {
        // Configuração
        String circuitName = "test-circuit";
        ResilienceManager.resetCircuit(circuitName);
        
        // Executar falhas
        for (int i = 0; i < 10; i++) {
            ResilienceManager.executeWithCircuitBreaker(
                circuitName,
                () -> { throw new RuntimeException("Simulated failure"); },
                () -> "fallback"
            );
        }
        
        // Verificar estado
        CircuitBreaker cb = ResilienceManager.getCircuitBreaker(circuitName);
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());
        
        // Testar fallback
        String result = ResilienceManager.executeWithCircuitBreaker(
            circuitName,
            () -> "success",
            () -> "fallback"
        );
        
        assertEquals("fallback", result);
    }
}