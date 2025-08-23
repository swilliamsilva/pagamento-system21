package com.pagamento.common.resilience;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;

class ResilienceConfigTest {

    @Test
    void shouldCreateCircuitBreakerRegistry() {
        // Configuração
        ResilienceRegistryConfig config = new ResilienceRegistryConfig();
        
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
        ResilienceRegistryConfig config = new ResilienceRegistryConfig();
        RetryConfigCustomizer customizer = RetryConfigCustomizer.of("default", builder -> {});
        
        // Execução
        RetryRegistry registry = config.retryRegistry(customizer);
        
        // Verificação
        assertNotNull(registry, "RetryRegistry não deve ser nulo");
        assertNotNull(registry.retry("default"), 
            "Retry padrão deve existir");
    }
}

