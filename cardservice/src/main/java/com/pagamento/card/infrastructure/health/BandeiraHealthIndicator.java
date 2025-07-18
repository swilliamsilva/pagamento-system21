package com.pagamento.card.infrastructure.health;

import com.pagamento.core.common.resilience.ResilienceManager;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BandeiraHealthIndicator implements HealthIndicator {
    
    private final Map<String, CircuitBreaker> circuitBreakers;

    public BandeiraHealthIndicator() {
        this.circuitBreakers = ResilienceManager.getAllCircuitBreakers();
    }

    @Override
    public Health health() {
        Health.Builder builder = Health.up();
        
        circuitBreakers.forEach((name, cb) -> {
            String status = cb.getState().name();
            builder.withDetail(name, status);
            
            if (CircuitBreaker.State.OPEN.equals(cb.getState())) {
                builder.down().withDetail(name + "_failureRate", cb.getMetrics().getFailureRate());
            }
        });
        
        return builder.build();
    }
}