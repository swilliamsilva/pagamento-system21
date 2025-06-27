package com.pagamento.gateway.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {

    @InjectMocks
    private SwaggerConfig swaggerConfig;
    
    @Mock
    private CorsConfigurationSource configSource;

    @Test
    void corsWebFilter_shouldAllowConfiguredOrigins() {
        // Act
        CorsWebFilter filter = swaggerConfig.corsWebFilter();
        
        // Assert
        assertNotNull(filter);
        
        // Teste de solicitação CORS
        ServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.options("/v3/api-docs")
                .header("Origin", "https://pagamento.com.br")
                .header("Access-Control-Request-Method", "GET")
        );
        
        assertTrue(CorsUtils.isCorsRequest(exchange.getRequest()));
        assertNotNull(configSource.getCorsConfiguration(exchange));
    }

    @Test
    void corsWebFilter_shouldBlockUnauthorizedOrigin() {
        // Configura
        SwaggerConfig config = new SwaggerConfig();
        config.setAllowedOrigins(new String[]{"https://trusted-domain.com"});
        
        // Act
        CorsWebFilter filter = config.corsWebFilter();
        
        // Teste
        ServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.options("/v3/api-docs")
                .header("Origin", "https://malicious-site.com")
                .header("Access-Control-Request-Method", "GET")
        );
        
        CorsConfiguration configResult = configSource.getCorsConfiguration(exchange);
        assertNull(configResult.getAllowedOrigins());
    }
}