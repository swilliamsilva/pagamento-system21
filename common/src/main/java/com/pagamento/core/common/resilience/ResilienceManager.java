package com.pagamento.core.common.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import java.time.Duration;
import java.util.function.Supplier;

public final class ResilienceManager {

    private ResilienceManager() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instanciada");
    }

    private static final CircuitBreakerRegistry registry = 
        CircuitBreakerRegistry.ofDefaults();
    
    public static <T> T executeWithCircuitBreaker(String name, 
                                                 Supplier<T> supplier, 
                                                 Supplier<T> fallback) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(10)
            .build();
            
        CircuitBreaker circuitBreaker = registry.circuitBreaker(name, config);
        
        return circuitBreaker.executeSupplier(
            () -> {
                try {
                    return supplier.get();
                } catch (Exception e) {
                    return fallback.get();
                }
            }
        );
    }
}