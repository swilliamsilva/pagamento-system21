package com.pagamento.pix.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

class ResilienceConfigTest {

    @Test
    void circuitBreaker_deveEstarConfiguradoCorretamente() {
        CircuitBreakerConfig config = ResilienceConfig.circuitBreakerConfig();
        
        assertEquals(50, config.getFailureRateThreshold());
        
        // Validação correta usando assertNotNull
        assertNotNull(config.getWaitIntervalFunctionInOpenState());
        
        assertEquals(5, config.getSlidingWindowSize());
    }
}