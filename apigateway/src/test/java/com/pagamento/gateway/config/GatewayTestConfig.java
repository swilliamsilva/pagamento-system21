package com.pagamento.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class GatewayTestConfig {

    @Value("${wiremock.server.port}")
    private int wiremockPort;

    @Bean
    public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
        String baseUri = "http://localhost:" + wiremockPort;

        return builder.routes()
            .route("pix-test", r -> r.path("/pix/test")
                .filters(f -> f.stripPrefix(1))
                .uri(baseUri))
            .route("fallback-pix", r -> r.path("/fallback/pix")
                .filters(f -> f.stripPrefix(1))
                .uri(baseUri))
            .route("api-data", r -> r.path("/api/data")
                .filters(f -> f.stripPrefix(1))
                .uri(baseUri))
            .route("api-limited", r -> r.path("/api/limited")
                .filters(f -> f.stripPrefix(1)
                    .requestRateLimiter(config -> {
                        // Se não quiser configurar nada aqui, apenas deixe vazio.
                        // A config será carregada automaticamente pelo application.yml
                    }))
                .uri(baseUri))
            .build();
    }
}
