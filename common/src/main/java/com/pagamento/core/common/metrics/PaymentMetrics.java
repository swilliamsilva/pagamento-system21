package com.pagamento.core.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentMetrics {
    
    private final MeterRegistry registry;
    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();
    private final Timer globalProcessingTimer;
    
    public PaymentMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.globalProcessingTimer = Timer.builder("payment.processing.time")
            .description("Tempo de processamento de pagamentos")
            .register(registry);
    }
    
    public Timer getProcessingTimer() {
        return globalProcessingTimer;
    }
    
    public void incrementStatus(String bandeira, PaymentStatus status) {
        String counterName = "payment.status." + status.name().toLowerCase();
        Counter counter = counters.computeIfAbsent(
            counterName,
            name -> Counter.builder(name)
                .tag("bandeira", bandeira)
                .tag("status", status.name())
                .register(registry)
        );
        counter.increment();
    }
    
    public void incrementError(String bandeira, String errorType) {
        String counterName = "payment.errors";
        Counter counter = counters.computeIfAbsent(
            counterName,
            name -> Counter.builder(name)
                .tag("bandeira", bandeira)
                .tag("error_type", errorType)
                .register(registry)
        );
        counter.increment();
    }
}