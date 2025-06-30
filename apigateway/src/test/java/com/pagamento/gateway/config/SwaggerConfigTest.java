package com.pagamento.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void corsWebFilter_shouldAllowConfiguredOrigins() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        swaggerConfig.setAllowedOrigins(new String[]{"https://pagamento.com.br"});
        swaggerConfig.setAllowedMethods(new String[]{"GET", "POST"});
        swaggerConfig.setAllowedHeaders(new String[]{"Content-Type"});
        swaggerConfig.setExposedHeaders(new String[]{"Authorization"});

        CorsWebFilter filter = swaggerConfig.corsWebFilter();
        assertNotNull(filter);

        // URI com host diferente do Origin (para ser CORS)
        ServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.options("https://outrohost.com/v3/api-docs")
                .header("Origin", "https://pagamento.com.br")
                .header("Access-Control-Request-Method", "GET")
                .build()
        );

        assertTrue(CorsUtils.isCorsRequest(exchange.getRequest()));
    }

    @Test
    void corsWebFilter_shouldBlockUnauthorizedOrigin() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        swaggerConfig.setAllowedOrigins(new String[]{"https://trusted-domain.com"});
        swaggerConfig.setAllowedMethods(new String[]{"GET"});
        swaggerConfig.setAllowedHeaders(new String[]{"Content-Type"});
        swaggerConfig.setExposedHeaders(new String[]{"Authorization"});

        CorsWebFilter filter = swaggerConfig.corsWebFilter();
        assertNotNull(filter);

        ServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.options("https://outrohost.com/v3/api-docs")
                .header("Origin", "https://malicious-site.com")
                .header("Access-Control-Request-Method", "GET")
                .build()
        );

        assertTrue(CorsUtils.isCorsRequest(exchange.getRequest()));
    }
}
