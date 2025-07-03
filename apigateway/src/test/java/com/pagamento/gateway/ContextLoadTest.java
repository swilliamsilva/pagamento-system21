package com.pagamento.gateway;

import com.pagamento.gateway.fallback.FallbackController;
import com.pagamento.gateway.filters.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.cloud.gateway.enabled=true",
    "spring.main.web-application-type=reactive",
    "filters.local-rate-limit.enabled=true",
    "security.admin-key=test-key",
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class ContextLoadTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextoDeveSerCarregado() {
        assertNotNull(context, "O contexto da aplicação deve ser carregado corretamente");
    }

    // Teste para verificar beans de filtros essenciais
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

    // Teste para verificar beans de controllers
    @Test
    void deveExistirControladorDeFallback() {
        assertNotNull(context.getBean(FallbackController.class), "Controlador de fallback deve estar presente");
    }

    // Teste para verificar beans de configuração
    @Test
    void deveExistirDefinidorDeRotas() {
        assertNotNull(context.getBean(RouteLocator.class), "Definidor de rotas deve estar presente");
    }

    // Teste para verificar beans de integração
    @Test
    void deveExistirLimitadorDeTaxaRedis() {
        assertNotNull(context.getBean("redisRateLimiter"), "Limitador de taxa Redis deve estar presente");
    }

    @Test
    void deveExistirResolvedorDeChaveDeApi() {
        assertNotNull(context.getBean("apiKeyResolver"), "Resolvedor de chave de API deve estar presente");
    }
    
    @Test
    void deveTerOrdemCorretaNosFiltros() {
        LoggingFilter loggingFilter = context.getBean(LoggingFilter.class);
        SecurityFilter securityFilter = context.getBean(SecurityFilter.class);
        RateLimitingFilter rateLimitingFilter = context.getBean(RateLimitingFilter.class);
        CircuitBreakerStateFilter circuitBreakerFilter = context.getBean(CircuitBreakerStateFilter.class);
        
        assertTrue(securityFilter.getOrder() < loggingFilter.getOrder(), 
                  "Filtro de segurança deve executar antes do logging");
        
        assertTrue(rateLimitingFilter.getOrder() < circuitBreakerFilter.getOrder(), 
                  "Limitação de taxa deve executar antes do circuit breaker");
    }
}