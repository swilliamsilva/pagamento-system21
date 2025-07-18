package com.pagamento.core.common.resilience;

import java.time.Duration;
import java.util.Set;
import java.util.function.Supplier;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

public class ResilienceManager {

    private static final CircuitBreakerRegistry registry = 
        CircuitBreakerRegistry.ofDefaults();
    
    // Private constructor to prevent instantiation
    private ResilienceManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
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

    public static Set<CircuitBreaker> getAllCircuitBreakers() {
        return registry.getAllCircuitBreakers();
    }
}