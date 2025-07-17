package com.pagamento.pix.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ResilienceConfigTest { // Removido o modificador 'public'

    @Test
    void circuitBreaker_deveEstarConfiguradoCorretamente() {
        CircuitBreakerConfig config = ResilienceConfig.circuitBreakerConfig();
        
        assertEquals(50, config.getFailureRateThreshold());
        assertEquals(Duration.ofMillis(1000), config.getWaitIntervalFunctionInOpenState());
        assertEquals(5, config.getSlidingWindowSize());
    }
}