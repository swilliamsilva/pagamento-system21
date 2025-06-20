package com.pagamento.common.health;

import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;

/**
 * Verifica se o serviço está vivo (liveness).
 * Ideal para detectar travamentos no container.
 */
@Component("liveness")
public class LivenessProbe implements HealthIndicator {

    @Override
    public Health health() {
        // Se esse método falhar, o container pode ser reiniciado
        return Health.up().withDetail("status", "Service is alive").build();
    }
}
