package com.pagamento.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

<Questions>
</Questions>
</Question>
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.pagamento.gateway.fallback.FallbackController;
import com.pagamento.gateway.filters.CircuitBreakerStateFilter;
import com.pagamento.gateway.filters.LoggingFilter;
import com.pagamento.gateway.filters.RateLimitingFilter;
import com.pagamento.gateway.filters.SecurityFilter;

@SpringBootTest
@Testcontainers
class ContextLoadTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private ApplicationContext context;

    @Test
    void contextoDeveSerCarregado() {
        assertNotNull(context, "O contexto da aplicação deve ser carregado corretamente");
    }

    @Test
    void deveExistirFiltroDeLog() {
        assertNotNull(context.getBean(LoggingFilter.class), "Filtro de logging deve estar presente");
    }

    @Test
    void deveExistirFiltroDeSeguranca() {
        assertNotNull(context.getBean(SecurityFilter.class), "Filtro de segurança deve estar presente");
    }

    @Test
    void deveExistirFiltroDeLimitacaoDeTaxa() {
        assertNotNull(context.getBean(RateLimitingFilter.class), "Filtro de limitação de taxa deve estar presente");
    }
    
    @Test
    void deveExistirFiltroDeEstadoDoCircuitBreaker() {
        assertNotNull(context.getBean(CircuitBreakerStateFilter.class), "Filtro de estado do Circuit Breaker deve estar presente");
    }

    @Test
    void deveExistirControladorDeFallback() {
        assertNotNull(context.getBean(FallbackController.class), "Controlador de fallback deve estar presente");
    }

    @Test
    void deveExistirDefinidorDeRotas() {
        assertNotNull(context.getBean(RouteLocator.class), "Definidor de rotas deve estar presente");
    }

    @Test
    void deveTerOrdemCorretaNosFiltros() {
        SecurityFilter securityFilter = context.getBean(SecurityFilter.class);
        LoggingFilter loggingFilter = context.getBean(LoggingFilter.class);
        RateLimitingFilter rateLimitingFilter = context.getBean(RateLimitingFilter.class);
        CircuitBreakerStateFilter circuitBreakerFilter = context.getBean(CircuitBreakerStateFilter.class);
        
        assertTrue(securityFilter.getOrder() < loggingFilter.getOrder(), 
                  "Filtro de segurança deve executar antes do logging");
        
        assertTrue(rateLimitingFilter.getOrder() < circuitBreakerFilter.getOrder(), 
                  "Limitação de taxa deve executar antes do circuit breaker");
    }
}