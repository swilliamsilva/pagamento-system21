package com.pagamento.gateway.filters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @InjectMocks
    private SecurityFilter filtroSeguranca;

    @Mock
    private GatewayFilterChain cadeiaFiltros;

    private MockServerWebExchange criarExchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }

    @Test
    void deveBloquearUrlsComPontoEVirgula() {
        ServerWebExchange exchange = criarExchange("/teste;param=ataque");
        
        Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
        
        StepVerifier.create(resultado).verifyComplete();
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode(),
            "Deveria bloquear URLs com ponto-e-vírgula (possível ataque)");
    }

    @Test
    void deveBloquearAcessoAEndpointsRestritosSemChaveAdmin() {
        ServerWebExchange exchange = criarExchange("/actuator/health");
        
        Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
        
        StepVerifier.create(resultado).verifyComplete();
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode(),
            "Deveria bloquear acesso a endpoints restritos sem chave administrativa");
    }

    @Test
    void devePermitirAcessoAEndpointsRestritosComChaveAdminValida() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/health")
            .header("X-Admin-Key", "ADMIN_SECRET_123")
            .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        when(cadeiaFiltros.filter(any())).thenReturn(Mono.empty());
        
        Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
        
        StepVerifier.create(resultado).verifyComplete();
        assertNull(exchange.getResponse().getStatusCode(),
            "Deveria permitir acesso com chave admin válida (status não alterado)");
    }

    @Test
    void deveBloquearTentativasDeInjecaoSQL() {
        String[] palavrasChave = {
            "/usuarios?comando=DELETE",
            "/produtos?query=DROP",
            "/pedidos?sql=INSERT",
            "/dados?filtro=SELECT"
        };
        
        for (String url : palavrasChave) {
            ServerWebExchange exchange = criarExchange(url);
            
            Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
            
            StepVerifier.create(resultado).verifyComplete();
            assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode(),
                "Deveria bloquear URL com possível injeção SQL: " + url);
        }
    }

    @Test
    void devePermitirUrlsValidasESeguras() {
        String[] urlsValidas = {
            "/api/recursos",
            "/clientes/123",
            "/produtos?categoria=eletronicos",
            "/pagamentos/status"
        };
        
        for (String url : urlsValidas) {
            ServerWebExchange exchange = criarExchange(url);
            when(cadeiaFiltros.filter(any())).thenReturn(Mono.empty());
            
            Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
            
            StepVerifier.create(resultado).verifyComplete();
            assertNull(exchange.getResponse().getStatusCode(),
                "Deveria permitir URL válida: " + url);
        }
    }

    @Test
    void deveBloquearChavesAdministrativasInvalidas() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/env")
            .header("X-Admin-Key", "CHAVE_INVALIDA")
            .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
        
        StepVerifier.create(resultado).verifyComplete();
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode(),
            "Deveria bloquear acesso com chave administrativa inválida");
    }

    @Test
    void deveBloquearTentativasDePathTraversal() {
        String[] pathsPerigosos = {
            "/api/../etc/passwd",
            "/diretorio/../../arquivos.conf",
            "/download?file=../../senhas.txt"
        };
        
        for (String path : pathsPerigosos) {
            ServerWebExchange exchange = criarExchange(path);
            
            Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
            
            StepVerifier.create(resultado).verifyComplete();
            assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode(),
                "Deveria bloquear tentativa de path traversal: " + path);
        }
    }
}