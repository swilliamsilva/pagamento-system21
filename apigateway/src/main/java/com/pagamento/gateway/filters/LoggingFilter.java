package com.pagamento.gateway.filters;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private final Logger logger;

    private static final String CORRELATION_ID = "X-Correlation-Id";
    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
        "authorization", "proxy-authorization", "cookie", "set-cookie", "x-api-key"
    );

    public LoggingFilter() {
        this(LoggerFactory.getLogger(LoggingFilter.class));
    }

    // Construtor para testes
    LoggingFilter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final String correlationId = getOrGenerateCorrelationId(request);

        // Armazena o correlation ID no contexto reativo
        Context context = Context.of(CORRELATION_ID, correlationId);
        
        return Mono.just(exchange)
            .flatMap(ex -> {
                final ServerWebExchange modifiedExchange = getModifiedExchange(ex, request, correlationId);
                modifiedExchange.getAttributes().put(REQUEST_START_TIME, System.currentTimeMillis());
                logRequestDetails(modifiedExchange.getRequest(), correlationId);
                return chain.filter(modifiedExchange);
            })
            .doFinally(signalType -> {
                Long startTime = exchange.getAttribute(REQUEST_START_TIME);
                if (startTime != null) {
                    long duration = System.currentTimeMillis() - startTime;
                    logResponseDetails(exchange, correlationId, duration);
                }
            })
            .contextWrite(context);
    }

    private ServerWebExchange getModifiedExchange(
        ServerWebExchange exchange, 
        ServerHttpRequest request,
        String correlationId
    ) {
        if (!request.getHeaders().containsKey(CORRELATION_ID)) {
            return exchange.mutate().request(
                request.mutate()
                    .header(CORRELATION_ID, correlationId)
                    .build()
            ).build();
        }
        return exchange;
    }

    private String getOrGenerateCorrelationId(ServerHttpRequest request) {
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID);
        return (correlationId != null && !correlationId.isBlank())
            ? correlationId
            : UUID.randomUUID().toString();
    }

    private void logRequestDetails(ServerHttpRequest request, String correlationId) {
        if (!logger.isInfoEnabled()) return;

        StringBuilder logMessage = new StringBuilder(256)
            .append("Request [").append(correlationId).append("]: ")
            .append(request.getMethod()).append(" ")
            .append(request.getURI());

        if (logger.isDebugEnabled()) {
            appendFilteredHeaders(logMessage, request.getHeaders());
        }

        logger.info(logMessage.toString());
    }

    private void appendFilteredHeaders(StringBuilder builder, HttpHeaders headers) {
        if (headers == null || headers.isEmpty()) return;
        
        builder.append("\nHeaders:");
        headers.forEach((name, values) -> {
            if (!isSensitiveHeader(name)) {
                builder.append("\n  ")
                       .append(name)
                       .append(": ")
                       .append(values.size() > 1 ? values : values.get(0));
            }
        });
    }

    private void logResponseDetails(ServerWebExchange exchange, String correlationId, long duration) {
        if (!logger.isInfoEnabled()) return;

        ServerHttpResponse response = exchange.getResponse();
        HttpStatusCode status = response.getStatusCode();
        String path = exchange.getRequest().getPath().toString();

        logger.info("Response [{}]: Status {} | Duration {}ms | Path: {}",
            correlationId,
            status != null ? status.value() : "N/A",
            duration,
            path);
    }

    private boolean isSensitiveHeader(String headerName) {
        return headerName != null && 
               SENSITIVE_HEADERS.contains(headerName.toLowerCase());
    }

    @Override
    public int getOrder() {
        // Alterado para alta prioridade mas não mínima absoluta
        return Ordered.HIGHEST_PRECEDENCE + 1000;
    }
}