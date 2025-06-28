package com.pagamento.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class GatewayApplication {

    private static final Logger logger = LoggerFactory.getLogger(GatewayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("pix-service", r -> r.path("/pix/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .retry(config -> config
                        .setRetries(3)
                        .setMethods(HttpMethod.GET, HttpMethod.POST)  // Varargs
                        .setStatuses(HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.GATEWAY_TIMEOUT)  // Varargs
                        .setBackoff(
                            Duration.ofMillis(100), 
                            Duration.ofSeconds(2),
                            2, 
                            true
                        ))
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
            
            .route("swagger-api", r -> r.path("/v3/api-docs/**")
                .filters(f -> f.rewritePath(
                    "/v3/api-docs/(?<service>.*)", 
                    "/${service}/v3/api-docs"))
                .uri("lb://${service}"))
            
            .route("swagger-ui", r -> r.path("/swagger-ui/**")
                .filters(f -> f.rewritePath(
                    "/swagger-ui/(?<path>.*)", 
                    "/${path}/swagger-ui.html"))
                .uri("lb://documentation-service"))
            .build();
    }
  /** 
    @Bean
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            logger.info("Request recebido: {} {}", 
                request.getMethod(), 
                request.getPath());
            
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                logger.info("Response enviado: {} {}", 
                    request.getPath(), 
                    response.getStatusCode());
            }));
        };
    }

**/
/**
    @Bean
    public GlobalFilter correlationFilter() {
        return (exchange, chain) -> {
            String correlationId = UUID.randomUUID().toString();
            
            // Adiciona header em requisições downstream
            ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Correlation-ID", correlationId)
                .build();
            
            // Adiciona header na resposta ao cliente
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("X-Correlation-ID", correlationId);
            
            return chain.filter(exchange.mutate().request(request).build());
        };
    }
   **/
    /***
    @Bean
    public GlobalFilter securityFilter() {
        return (exchange, chain) -> {
            // Validação básica de segurança
            if (exchange.getRequest().getPath().toString().contains(";")) {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        };
    }
    **/
}