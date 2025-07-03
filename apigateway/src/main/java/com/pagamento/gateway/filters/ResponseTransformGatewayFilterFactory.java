package com.pagamento.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ResponseTransformGatewayFilterFactory 
    extends AbstractGatewayFilterFactory<ResponseTransformGatewayFilterFactory.Config> {

    public ResponseTransformGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            if (config.isEnabled() && exchange.getResponse().getStatusCode().is2xxSuccessful()) {
                ServerHttpResponse response = exchange.getResponse();
                
                // 1. Adiciona header de transformação
                response.getHeaders().add("X-Response-Transformed", "true");
                
                // 2. Padroniza tipo de conteúdo
                if (!response.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE)) {
                    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
                }
            }
        }));
    }

    public static class Config {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}