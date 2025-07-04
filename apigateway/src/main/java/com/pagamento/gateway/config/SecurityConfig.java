package com.pagamento.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.server.WebFilter;

@Configuration
public class SecurityConfig {

    @Bean
    @Profile("!prod")
    public WebFilter devSecurityFilter() {
        return (exchange, chain) -> chain.filter(exchange); // Sem segurança
    }

    @Bean
    @Profile("prod")
    public WebFilter prodSecurityFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            
            // Lista de endpoints públicos
            if (path.startsWith("/auth") || 
                path.startsWith("/v3/api-docs") || 
                path.startsWith("/swagger-ui") || 
                path.startsWith("/fallback") || 
                path.equals("/actuator/health")) {
                return chain.filter(exchange);
            }
            
            // Verificação simples de API Key
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");
            if (apiKey == null || !apiKey.equals("sua-chave-secreta")) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
            return chain.filter(exchange);
        };
    }
}