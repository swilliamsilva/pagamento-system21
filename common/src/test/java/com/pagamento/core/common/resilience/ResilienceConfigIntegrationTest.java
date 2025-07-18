package com.pagamento.core.common.resilience;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@SpringBootTest
class ResilienceConfigIntegrationTest {
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    @Test
    void shouldAutowireRegistry() {
        assertNotNull(circuitBreakerRegistry);
    }
}