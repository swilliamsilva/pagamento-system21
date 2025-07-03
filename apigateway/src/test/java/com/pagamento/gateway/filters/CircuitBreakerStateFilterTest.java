package com.pagamento.gateway.filters;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o filtro de estado do Circuit Breaker.
 * 
 * <p>Verifica o comportamento do filtro em relação a:
 * <ul>
 *   <li>Adição do header de estado do Circuit Breaker</li>
 *   <li>Ordem de execução do filtro</li>
 *   <li>Propagação correta da requisição pela cadeia de filtros</li>
 * </ul>
 */
class CircuitBreakerStateFilterTest {

    /**
     * Verifica se o filtro adiciona corretamente o header de estado do Circuit Breaker.
     * 
     * <p>O header "X-Circuit-Breaker-State" deve ser adicionado à resposta com o valor "closed".
     */
    @Test
    void deveAdicionarHeaderDeEstadoDoCircuitBreaker() {
        // Configuração
        CircuitBreakerStateFilter filtro = new CircuitBreakerStateFilter();
        MockServerHttpRequest requisicao = MockServerHttpRequest.get("/teste").build();
        ServerWebExchange exchange = MockServerWebExchange.from(requisicao);
        GatewayFilterChain cadeia = e -> Mono.empty(); // Cadeia vazia
        
        // Execução
        Mono<Void> resultado = filtro.filter(exchange, cadeia);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        String valorHeader = exchange.getResponse().getHeaders().getFirst("X-Circuit-Breaker-State");
        assertEquals("closed", valorHeader, "O header deve ter valor 'closed'");
    }

    /**
     * Verifica se o filtro tem a menor precedência de ordem.
     * 
     * <p>O filtro deve ser executado por último na cadeia de filtros.
     */
    @Test
    void deveTerPrecedenciaMaisBaixa() {
        // Configuração
        CircuitBreakerStateFilter filtro = new CircuitBreakerStateFilter();
        
        // Verificação
        assertEquals(Ordered.LOWEST_PRECEDENCE, filtro.getOrder(), 
                     "O filtro deve ter a menor precedência");
    }

    /**
     * Verifica se o filtro não interrompe o fluxo da requisição.
     * 
     * <p>O filtro deve garantir que a cadeia de filtros subsequente seja executada.
     */
    @Test
    void naoDeveInterromperFluxoDaRequisicao() {
        // Configuração
        CircuitBreakerStateFilter filtro = new CircuitBreakerStateFilter();
        MockServerHttpRequest requisicao = MockServerHttpRequest.get("/teste").build();
        ServerWebExchange exchange = MockServerWebExchange.from(requisicao);
        
        // Sinalizador de execução da cadeia
        final boolean[] cadeiaExecutada = {false};
        GatewayFilterChain cadeia = e -> {
            cadeiaExecutada[0] = true;
            return Mono.empty();
        };
        
        // Execução
        Mono<Void> resultado = filtro.filter(exchange, cadeia);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        assertTrue(cadeiaExecutada[0], "A cadeia de filtros deve ter sido executada");
    }

    /**
     * Verifica se o header é adicionado mesmo quando a cadeia gera erro.
     * 
     * <p>O filtro deve adicionar o header mesmo em casos de exceção na cadeia.
     */
    @Test
    void deveAdicionarHeaderMesmoComErroNaCadeia() {
        // Configuração
        CircuitBreakerStateFilter filtro = new CircuitBreakerStateFilter();
        MockServerHttpRequest requisicao = MockServerHttpRequest.get("/teste").build();
        ServerWebExchange exchange = MockServerWebExchange.from(requisicao);
        
        // Cadeia que gera erro
        GatewayFilterChain cadeia = e -> Mono.error(new RuntimeException("Erro simulado"));
        
        // Execução
        Mono<Void> resultado = filtro.filter(exchange, cadeia);
        
        // Verificação
        StepVerifier.create(resultado)
            .expectError(RuntimeException.class)
            .verify();
        
        String valorHeader = exchange.getResponse().getHeaders().getFirst("X-Circuit-Breaker-State");
        assertEquals("closed", valorHeader, "O header deve ser adicionado mesmo com erro");
    }

    /**
     * Verifica se múltiplas execuções mantêm o comportamento consistente.
     */
    @Test
    void deveManterComportamentoConsistenteEmMultiplasExecucoes() {
        // Configuração
        CircuitBreakerStateFilter filtro = new CircuitBreakerStateFilter();
        
        for (int i = 0; i < 5; i++) {
            MockServerHttpRequest requisicao = MockServerHttpRequest.get("/teste-" + i).build();
            ServerWebExchange exchange = MockServerWebExchange.from(requisicao);
            GatewayFilterChain cadeia = e -> Mono.empty();
            
            // Execução
            Mono<Void> resultado = filtro.filter(exchange, cadeia);
            StepVerifier.create(resultado).verifyComplete();
            
            // Verificação
            String valorHeader = exchange.getResponse().getHeaders().getFirst("X-Circuit-Breaker-State");
            assertEquals("closed", valorHeader, "Header deve ser 'closed' na execução " + i);
        }
    }
}