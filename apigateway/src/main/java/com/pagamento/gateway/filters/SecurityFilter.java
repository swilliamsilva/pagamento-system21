package com.pagamento.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SecurityFilter implements GlobalFilter, Ordered {

    private static final Set<String> RESTRICTED_PATHS = Set.of(
        "/actuator", "/internal", "/admin"
    );
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        
        // 1. Validação de caminhos perigosos
        if (path.contains(";") || path.contains("..")) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }
        
        // 2. Validação de caminhos administrativos
        if (RESTRICTED_PATHS.stream().anyMatch(path::startsWith)) {
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-Admin-Key");
            if (!"ADMIN_SECRET_123".equals(apiKey)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }
        
        // 3. Proteção básica contra SQL injection
        if (path.toLowerCase().contains("delete") || 
            path.toLowerCase().contains("drop") || 
            path.toLowerCase().contains("insert")) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}