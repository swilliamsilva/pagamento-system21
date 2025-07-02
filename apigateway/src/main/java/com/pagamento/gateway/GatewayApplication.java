package com.pagamento.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.Duration;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // Rotas reais, só carregam se o profile NÃO for 'test'
    @Bean
    @Profile("!test")
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("pix-service", r -> r.path("/pix/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .retry(config -> config
                        .setRetries(3)
                        .setMethods(HttpMethod.GET, HttpMethod.POST)
                        .setStatuses(HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.GATEWAY_TIMEOUT)
                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(2), 2, true))
                    .circuitBreaker(config -> config
                        .setName("pixCircuitBreaker")
                        .setFallbackUri("forward:/fallback/pix")))
                .uri("lb://pix-service"))

            .route("auth-service", r -> r.path("/auth/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .circuitBreaker(config -> config
                        .setName("authCircuitBreaker")
                        .setFallbackUri("forward:/fallback/auth")))
                .uri("lb://auth-service"))

            .route("swagger-pix", r -> r.path("/v3/api-docs/pix")
                .uri("lb://pix-service"))

            .route("swagger-auth", r -> r.path("/v3/api-docs/auth")
                .uri("lb://auth-service"))

            .route("swagger-ui", r -> r.path("/swagger-ui/**")
                .filters(f -> f.rewritePath("/swagger-ui/(?<path>.*)", "/${path}/swagger-ui.html"))
                .uri("lb://documentation-service"))

            .build();
    }
}