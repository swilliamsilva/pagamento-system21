package com.pagamento.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Set;
import java.util.UUID;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String CORRELATION_ID = "X-Correlation-Id";
    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
        "authorization", "proxy-authorization", "cookie", "set-cookie", "x-api-key"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Gera correlation ID se não existir
        final String correlationId = getOrGenerateCorrelationId(exchange.getRequest());
        
        // 2. Cria exchange modificada com novo header se necessário
        final ServerWebExchange modifiedExchange = exchange.getRequest().getHeaders().containsKey(CORRELATION_ID)
            ? exchange
            : exchange.mutate().request(
                    exchange.getRequest().mutate()
                        .header(CORRELATION_ID, correlationId)
                        .build()
                ).build();

        // 3. Registra tempo de início
        modifiedExchange.getAttributes().put(REQUEST_START_TIME, System.currentTimeMillis());
        
        // 4. Log da requisição
        logRequestDetails(modifiedExchange.getRequest(), correlationId);

        // 5. Processa a requisição e loga a resposta usando contexto reativo
        return chain.filter(modifiedExchange)
            .doOnEach(signal -> {
                if (signal.isOnComplete()) {
                    long duration = System.currentTimeMillis() - 
                        (long) modifiedExchange.getAttribute(REQUEST_START_TIME);
                    logResponseDetails(modifiedExchange, correlationId, duration);
                }
            })
            .contextWrite(Context.of(CORRELATION_ID, correlationId)); // Contexto reativo
    }

    private String getOrGenerateCorrelationId(ServerHttpRequest request) {
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID);
        return (correlationId == null || correlationId.isBlank())
            ? UUID.randomUUID().toString()
            : correlationId;
    }

    private void logRequestDetails(ServerHttpRequest request, String correlationId) {
        if (logger.isInfoEnabled()) {
            StringBuilder logMessage = new StringBuilder(256)
                .append("Request [").append(correlationId).append("]: ")
                .append(request.getMethod()).append(" ")
                .append(request.getURI());
            
            if (logger.isDebugEnabled()) {
                appendFilteredHeaders(logMessage, request.getHeaders());
            }
            
            logger.info(logMessage.toString());
        }
    }

    private void appendFilteredHeaders(StringBuilder builder, HttpHeaders headers) {
        builder.append("\nHeaders:");
        headers.forEach((name, values) -> {
            if (!isSensitiveHeader(name)) {
                builder.append("\n  ").append(name).append(": ").append(values);
            }
        });
    }

    private void logResponseDetails(ServerWebExchange exchange, String correlationId, long duration) {
        int statusCode = exchange.getResponse().getStatusCode() != null ?
                         exchange.getResponse().getStatusCode().value() : 0;
        
        logger.info("Response [{}]: Status {} | Duration {}ms | Path: {}", 
            correlationId, 
            statusCode, 
            duration,
            exchange.getRequest().getPath());
    }

    private boolean isSensitiveHeader(String headerName) {
        return SENSITIVE_HEADERS.contains(headerName.toLowerCase());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}