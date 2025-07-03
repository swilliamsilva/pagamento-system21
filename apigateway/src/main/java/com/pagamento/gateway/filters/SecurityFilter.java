package com.pagamento.gateway.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class SecurityFilter implements GlobalFilter, Ordered {

    private static final Set<String> RESTRICTED_PATHS = Set.of(
        "/actuator", "/internal", "/admin"
    );
    
    @Value("${security.admin-key}")
    private String adminKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        
        // 1. Proteção contra path traversal
        if (isPathTraversalAttempt(path)) {
            return handleBlockedRequest(exchange, "Tentativa de Path Traversal detectada");
        }
        
        // 2. Validação de caminhos administrativos
        if (isRestrictedPath(path)) {
            return validateAdminAccess(exchange, chain); // Passar chain como parâmetro
        }
        
        // 3. Proteção contra injeção SQL nos parâmetros
        if (containsSqlInjection(exchange)) {
            return handleBlockedRequest(exchange, "Padrão de injeção SQL detectado");
        }
        
        return chain.filter(exchange);
    }

    private boolean isPathTraversalAttempt(String path) {
        return path.contains(";") || path.contains("..") || path.contains("//");
    }

    private boolean isRestrictedPath(String path) {
        return RESTRICTED_PATHS.stream().anyMatch(path::startsWith);
    }

    // Adicionado chain como parâmetro
    private Mono<Void> validateAdminAccess(ServerWebExchange exchange, GatewayFilterChain chain) {
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-Admin-Key");
        if (adminKey.equals(apiKey)) {
            return chain.filter(exchange); // Agora chain está disponível
        }
        return handleBlockedRequest(exchange, "Acesso administrativo não autorizado");
    }

    private boolean containsSqlInjection(ServerWebExchange exchange) {
        return exchange.getRequest().getQueryParams().values().stream()
            .anyMatch(values -> values.stream()
                .anyMatch(value -> 
                    value.toLowerCase().contains("delete ") ||
                    value.toLowerCase().contains("drop table") ||
                    value.toLowerCase().contains("insert into") ||
                    value.toLowerCase().contains("select *") ||
                    value.toLowerCase().contains("update ") ||
                    value.toLowerCase().contains("truncate ") ||
                    value.toLowerCase().contains("--") ||
                    value.toLowerCase().contains("' or ")
                )
            );
    }

    private Mono<Void> handleBlockedRequest(ServerWebExchange exchange, String reason) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN); // Alterado para 403
        exchange.getResponse().getHeaders().add("X-Blocked-Reason", reason);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}