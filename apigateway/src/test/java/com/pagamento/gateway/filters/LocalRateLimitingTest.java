package com.pagamento.gateway.filters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Testes para o filtro de limitação local de requisições.
 * 
 * <p>Esta classe de testes verifica o comportamento do limitador de requisições
 * baseado em memória (local) em diferentes cenários:</p>
 * 
 * <ul>
 *   <li>Requisições dentro do limite permitido</li>
 *   <li>Bloqueio após exceder o limite configurado</li>
 *   <li>Recuperação após período de espera</li>
 * </ul>
 * 
 * <p>Configuração do teste:
 * <ul>
 *   <li>Ambiente web com porta aleatória</li>
 *   <li>Perfil ativo: test-local-rate-limit</li>
 *   <li>Limite configurado: 5 requisições por minuto</li>
 * </ul>
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=test-local-rate-limit"
)
class LocalRateLimitingTest {

    @Autowired
    private RateLimitingFilter filtroLimitacao;

    @Autowired
    private WebTestClient webTestClient;

    /**
     * Limpa os registros de limitação após cada teste.
     * 
     * <p>Garante isolamento entre os testes, evitando interferência
     * de estado acumulado entre execuções.
     */
    @AfterEach
    void limparEstado() {
        filtroLimitacao.clearBuckets();
    }

    /**
     * Verifica se o sistema bloqueia requisições após exceder o limite configurado.
     * 
     * <p>Fluxo do teste:
     * <ol>
     *   <li>Executa 5 requisições bem-sucedidas (dentro do limite)</li>
     *   <li>Verifica que a 6ª requisição é bloqueada com status 429</li>
     *   <li>Valida o cabeçalho "Retry-After" com valor de 60 segundos</li>
     * </ol>
     */
    @Test
    void deveBloquearRequisicoesAposExcederLimite() {
        // Executar 5 requisições dentro do limite
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                .uri("/api/limited")
                .exchange()
                .expectStatus().isOk();
        }

        // Tentativa de 6ª requisição (deve ser bloqueada)
        webTestClient.get()
            .uri("/api/limited")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
            .expectHeader().valueEquals("Retry-After", "60");
    }

    /**
     * Verifica se o sistema permite novas requisições após o período de recarga.
     * 
     * <p>Fluxo do teste:
     * <ol>
     *   <li>Excede o limite de requisições</li>
     *   <li>Aguarda 60 segundos</li>
     *   <li>Verifica que novas requisições são permitidas</li>
     * </ol>
     */
    @Test
    void devePermitirRequisicoesAposPeriodoDeRecarga() throws InterruptedException {
        // Exceder o limite
        for (int i = 0; i < 6; i++) {
            webTestClient.get()
                .uri("/api/limited")
                .exchange();
        }

        // Aguardar período de recarga
        Thread.sleep(60000);

        // Nova requisição deve ser permitida
        webTestClient.get()
            .uri("/api/limited")
            .exchange()
            .expectStatus().isOk();
    }

    /**
     * Verifica se diferentes endpoints têm contadores independentes.
     * 
     * <p>Fluxo do teste:
     * <ol>
     *   <li>Excede o limite no endpoint /api/limited</li>
     *   <li>Verifica que outro endpoint (/api/outro) ainda aceita requisições</li>
     * </ol>
     */
    @Test
    void deveManterContadoresSeparadosParaDiferentesEndpoints() {
        // Exceder limite no endpoint principal
        for (int i = 0; i < 6; i++) {
            webTestClient.get()
                .uri("/api/limited")
                .exchange();
        }

        // Verificar acesso a outro endpoint
        webTestClient.get()
            .uri("/api/outro")
            .exchange()
            .expectStatus().isOk();
    }

    /**
     * Verifica se diferentes clientes têm contadores independentes.
     * 
     * <p>Fluxo do teste:
     * <ol>
     *   <li>Excede o limite com cliente A</li>
     *   <li>Verifica que cliente B ainda pode fazer requisições</li>
     * </ol>
     */
    @Test
    void deveManterContadoresSeparadosParaDiferentesClientes() {
        // Exceder limite com cliente A
        for (int i = 0; i < 6; i++) {
            webTestClient.get()
                .uri("/api/limited")
                .header("X-Client-ID", "cliente-A")
                .exchange();
        }

        // Cliente B deve poder acessar
        webTestClient.get()
            .uri("/api/limited")
            .header("X-Client-ID", "cliente-B")
            .exchange()
            .expectStatus().isOk();
    }
}