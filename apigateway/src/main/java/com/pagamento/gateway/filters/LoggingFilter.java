package com.pagamento.gateway.filters;

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
    		/**
    		 * 
    		 * Multiple markers at this line
	- Set cannot be resolved
	- Set cannot be resolved to a type
    		 * 
    		 * 
    		 * **/
    		
        "authorization", "proxy-authorization", "cookie", 
        "set-cookie", "x-api-key", "x-sensitive"
    );

    public LoggingFilter() {
        this(LoggerFactory.getLogger(LoggingFilter.class));
    }

    LoggingFilter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final String correlationId = getOrGenerateCorrelationId(request);

        // Adiciona correlation ID aos headers de resposta
        exchange.getResponse().getHeaders().add(CORRELATION_ID, correlationId);
        
        final ServerWebExchange modifiedExchange = mutateRequestIfNeeded(exchange, correlationId);
        modifiedExchange.getAttributes().put(REQUEST_START_TIME, System.currentTimeMillis());
        
        logRequestDetails(modifiedExchange.getRequest(), correlationId);
        
        return chain.filter(modifiedExchange)
            .doFinally(signalType -> {
                Long startTime = modifiedExchange.getAttribute(REQUEST_START_TIME);
                if (startTime != null) {
                    long duration = System.currentTimeMillis() - startTime;
                    logResponseDetails(modifiedExchange, correlationId, duration);
                }
            })
            .contextWrite(Context.of(CORRELATION_ID, correlationId));
    }

    private ServerWebExchange mutateRequestIfNeeded(
        ServerWebExchange exchange, 
        String correlationId
    ) {
        if (exchange.getRequest().getHeaders().containsKey(CORRELATION_ID)) {
            return exchange;
        }
        return exchange.mutate()
            .request(builder -> builder.header(CORRELATION_ID, correlationId))
            .build();
    }

    private String getOrGenerateCorrelationId(ServerHttpRequest request) {
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID);
        return (correlationId != null && !correlationId.isBlank())
            ? correlationId
            : UUID.randomUUID().toString();
    }

    private void logRequestDetails(ServerHttpRequest request, String correlationId) {
        if (!logger.isInfoEnabled()) return;

        StringBuilder logMessage = new StringBuilder()
            .append("Request [").append(correlationId).append("]: ")
            .append(request.getMethod()).append(" ")
            .append(request.getURI());

        if (logger.isDebugEnabled()) {
            logMessage.append("\nHeaders:");
            request.getHeaders().forEach((name, values) -> {
                if (!isSensitiveHeader(name)) {
                    logMessage.append("\n  ")
                             .append(name)
                             .append(": ")
                             .append(values.size() > 1 ? values : values.get(0));
                } else {
                    logMessage.append("\n  ")
                             .append(name)
                             .append(": *****"); // Mascarar valores sensíveis
                }
            });
        }

        logger.info(logMessage.toString());
    }

    private void logResponseDetails(ServerWebExchange exchange, String correlationId, long duration) {
        if (!logger.isInfoEnabled()) return;

        HttpStatusCode status = exchange.getResponse().getStatusCode();
        String path = exchange.getRequest().getPath().toString();

        // Formato padronizado que o teste espera
        logger.info("Response [{}]: Status {} | Duration {}ms | Path: {}",
            correlationId,
            (status != null ? status.value() : "N/A"),
            duration,
            path);
    }

    private boolean isSensitiveHeader(String headerName) {
        return headerName != null && 
               SENSITIVE_HEADERS.contains(headerName.toLowerCase());
        /**
         * 
         * Set cannot be resolved to a type
         * */
        
        
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1000;
    }
}