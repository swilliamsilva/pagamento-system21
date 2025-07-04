package com.pagamento.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuração de rotas para ambiente de testes.
 * 
 * <p>Define rotas simuladas para testes de integração do Gateway, utilizando o WireMock como backend.
 * Esta configuração é ativada apenas quando o perfil "test" está ativo.</p>
 * 
 * <p>Rotas configuradas:
 * <ul>
 *   <li>/pix/test - Testes de serviço PIX</li>
 *   <li>/fallback/pix - Testes de circuit breaker</li>
 *   <li>/api/data - Testes de transformação de dados</li>
 *   <li>/api/resource - Testes de recursos genéricos</li>
 *   <li>/api/limited - Testes de rate limiting</li>
 *   <li>/api/protegido - Testes principais de limitação de requisições</li>
 * </ul>
 */
@Configuration
@Profile("test")
public class GatewayTestConfig {

    @Value("${wiremock.server.port}")
    private int wiremockPort;

    /**
     * Configura as rotas de teste para integração com o WireMock.
     * 
     * @param builder Construtor de rotas do Spring Cloud Gateway
     * @return Locator de rotas configurado para ambiente de teste
     */
    @Bean
    public RouteLocator configurarRotasDeTeste(RouteLocatorBuilder builder) {
        String baseUri = "http://localhost:" + wiremockPort;

        return builder.routes()
            // Testes de serviço PIX
            .route("teste-pix", r -> r.path("/pix/test").uri(baseUri))
            
            // Testes de circuit breaker
            .route("fallback-pix", r -> r.path("/fallback/pix").uri(baseUri))
            
            // Testes de transformação de dados
            .route("dados-api", r -> r.path("/api/data").uri(baseUri))
            
            // Testes de recursos genéricos
            .route("recurso-api", r -> r.path("/api/resource").uri(baseUri))
            
            // Testes de rate limiting
            .route("limitacao-api", r -> r.path("/api/limited").uri(baseUri))
            
            // Testes principais de limitação de requisições
            .route("protegido-api", r -> r.path("/api/protegido").uri(baseUri))
            .build();
    }
}