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

/**
 * Testes para o filtro de segurança do Gateway.
 * 
 * <p>Verifica o comportamento do filtro em relação a:
 * <ul>
 *   <li>Bloqueio de URLs maliciosas com caracteres suspeitos</li>
 *   <li>Proteção de endpoints administrativos</li>
 *   <li>Detecção de tentativas de injeção SQL</li>
 *   <li>Permissão de tráfego legítimo</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @InjectMocks
    private SecurityFilter filtroSeguranca;

    @Mock
    private GatewayFilterChain cadeiaFiltros;

    /**
     * Cria um exchange simulado para o caminho especificado.
     * 
     * @param path O caminho da URL a ser testado
     * @return ServerWebExchange configurado para teste
     */
    private MockServerWebExchange criarExchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }

    /**
     * Verifica se o filtro bloqueia URLs com ponto-e-vírgula.
     * 
     * <p>Ponto-e-vírgula pode ser usado em ataques de manipulação de URL.
     */
    @Test
    void deveBloquearUrlsComPontoEVirgula() {
        // Configuração
        ServerWebExchange exchange = criarExchange("/teste;param=ataque");
        
        // Execução
        Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode(),
            "Deveria bloquear URLs com ponto-e-vírgula (possível ataque)");
    }

    /**
     * Verifica se o filtro bloqueia acesso a endpoints restritos sem chave administrativa.
     * 
     * <p>Endpoints como /actuator/** exigem chave de administração.
     */
    @Test
    void deveBloquearAcessoAEndpointsRestritosSemChaveAdmin() {
        // Configuração
        ServerWebExchange exchange = criarExchange("/actuator/health");
        
        // Execução
        Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode(),
            "Deveria bloquear acesso a endpoints restritos sem chave administrativa");
    }

    /**
     * Verifica se o filtro permite acesso a endpoints restritos com chave válida.
     * 
     * <p>O header X-Admin-Key deve conter o valor configurado.
     */
    @Test
    void devePermitirAcessoAEndpointsRestritosComChaveAdminValida() {
        // Configuração
        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/health")
            .header("X-Admin-Key", "ADMIN_SECRET_123")
            .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        when(cadeiaFiltros.filter(any())).thenReturn(Mono.empty());
        
        // Execução
        Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        assertNull(exchange.getResponse().getStatusCode(),
            "Deveria permitir acesso com chave admin válida (status não alterado)");
    }

    /**
     * Verifica se o filtro bloqueia tentativas de injeção SQL.
     * 
     * <p>Palavras-chave como SELECT, INSERT, DELETE, DROP, etc. devem ser detectadas.
     */
    @Test
    void deveBloquearTentativasDeInjecaoSQL() {
        // Testa várias palavras-chave de SQL
        String[] palavrasChave = {
            "/usuarios?comando=DELETE",
            "/produtos?query=DROP",
            "/pedidos?sql=INSERT",
            "/dados?filtro=SELECT"
        };
        
        for (String url : palavrasChave) {
            // Configuração
            ServerWebExchange exchange = criarExchange(url);
            
            // Execução
            Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
            
            // Verificação
            StepVerifier.create(resultado).verifyComplete();
            assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode(),
                "Deveria bloquear URL com possível injeção SQL: " + url);
        }
    }

    /**
     * Verifica se o filtro permite URLs válidas e seguras.
     */
    @Test
    void devePermitirUrlsValidasESeguras() {
        // URLs válidas para teste
        String[] urlsValidas = {
            "/api/recursos",
            "/clientes/123",
            "/produtos?categoria=eletronicos",
            "/pagamentos/status"
        };
        
        for (String url : urlsValidas) {
            // Configuração
            ServerWebExchange exchange = criarExchange(url);
            when(cadeiaFiltros.filter(any())).thenReturn(Mono.empty());
            
            // Execução
            Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
            
            // Verificação
            StepVerifier.create(resultado).verifyComplete();
            assertNull(exchange.getResponse().getStatusCode(),
                "Deveria permitir URL válida: " + url);
        }
    }

    /**
     * Verifica se o filtro bloqueia chaves administrativas inválidas.
     */
    @Test
    void deveBloquearChavesAdministrativasInvalidas() {
        // Configuração
        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/env")
            .header("X-Admin-Key", "CHAVE_INVALIDA")
            .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Execução
        Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode(),
            "Deveria bloquear acesso com chave administrativa inválida");
    }

    /**
     * Verifica se o filtro protege contra path traversal.
     */
    @Test
    void deveBloquearTentativasDePathTraversal() {
        // Padrões de path traversal
        String[] pathsPerigosos = {
            "/api/../etc/passwd",
            "/diretorio/../../arquivos.conf",
            "/download?file=../../senhas.txt"
        };
        
        for (String path : pathsPerigosos) {
            // Configuração
            ServerWebExchange exchange = criarExchange(path);
            
            // Execução
            Mono<Void> resultado = filtroSeguranca.filter(exchange, cadeiaFiltros);
            
            // Verificação
            StepVerifier.create(resultado).verifyComplete();
            assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode(),
                "Deveria bloquear tentativa de path traversal: " + path);
        }
    }
}