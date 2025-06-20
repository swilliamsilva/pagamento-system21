package com.pagamento.common.observability;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.springframework.context.annotation.Configuration;

/**
 * Exemplo básico de configuração de tracing manual.
 */
@Configuration
public class TracingConfig {

    private final Tracer tracer;

    public TracingConfig(Tracer tracer) {
        this.tracer = tracer;
    }

    public void startSpan(String spanName) {
        Span span = tracer.nextSpan().name(spanName).start();
        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
            // A lógica rastreada iria aqui
        } finally {
            span.end();
        }
    }
}
