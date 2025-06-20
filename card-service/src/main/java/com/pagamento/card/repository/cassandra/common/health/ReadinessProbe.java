package com.pagamento.common.health;

import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;

/**
 * Verifica se o serviço está pronto para receber tráfego (readiness).
 * Ideal para Kubernetes, por exemplo.
 */
@Component("readiness")
public class ReadinessProbe implements HealthIndicator {

    @Override
    public Health health() {
        // Lógica simplificada, pode incluir DB, cache, etc.
        return Health.up().withDetail("status", "Service is ready").build();
    }
}
