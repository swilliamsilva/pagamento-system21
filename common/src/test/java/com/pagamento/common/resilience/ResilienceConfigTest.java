package com.pagamento.common.resilience; // Pacote corrigido

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;

class ResilienceConfigTest {

    @Test
    void shouldCreateCircuitBreakerRegistry() {
        // Configuração
        ResilienceConfig config = new ResilienceConfig();
        
        // Execução
        CircuitBreakerRegistry registry = config.circuitBreakerRegistry();
        
        // Verificação
        assertNotNull(registry, "CircuitBreakerRegistry não deve ser nulo");
        assertNotNull(registry.circuitBreaker("default"), 
            "CircuitBreaker padrão deve existir");
    }

    @Test
    void shouldCreateRetryRegistry() {
        // Configuração
        ResilienceConfig config = new ResilienceConfig();
        
        // Execução
        RetryRegistry registry = config.retryRegistry();
        
        // Verificação
        assertNotNull(registry, "RetryRegistry não deve ser nulo");
        assertNotNull(registry.retry("default"), 
            "Retry padrão deve existir");
    }
}