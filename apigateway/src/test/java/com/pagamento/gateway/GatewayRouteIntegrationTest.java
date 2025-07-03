package com.pagamento.gateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "security.admin-key=test-admin-key"
})
class GatewayRouteIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void configurarStubs() {
        wireMockServer.resetAll();

        // Configuração para rota normal
        stubFor(get(urlEqualTo("/pix/test"))
            .willReturn(ok()
                .withHeader("Content-Type", "text/plain")
                .withBody("ok")));

        // Configuração para simular falha no serviço
        stubFor(get(urlEqualTo("/pix/falha"))
            .willReturn(serverError()
                .withFixedDelay(5000)));

        // Configuração do endpoint de fallback
        stubFor(get(urlEqualTo("/fallback/pix"))
            .willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"mensagem\":\"Fallback acionado\"}")));

        // Configuração do endpoint administrativo
        stubFor(get(urlEqualTo("/internal/status"))
            .willReturn(ok()
                .withBody("admin-ok")));
        
        // Configuração para rota de documentação
        stubFor(get(urlEqualTo("/v3/api-docs"))
            .willReturn(ok()
                .withBody("{\"openapi\":\"3.0.1\"}")));
    }

    @Test
    void deveRotejarParaServicoPix() {
        webTestClient.get()
            .uri("/pix/test")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("ok");
    }

    @Test
    void deveAcionarFallbackDoCircuitBreaker() {
        // Causa 5 falhas para abrir o circuit breaker
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                .uri("/pix/falha")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Verifica se o fallback é acionado
        webTestClient.get()
            .uri("/pix/falha")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.mensagem").isEqualTo("Fallback acionado");
    }

    @Test
    void devePropagarCorrelationId() {
        webTestClient.get()
            .uri("/pix/test")
            .header("X-Correlation-Id", "id-personalizado-123")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("X-Correlation-Id", "id-personalizado-123");
    }

    @Test
    void deveGerarCorrelationIdQuandoAusente() {
        webTestClient.get()
            .uri("/pix/test")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().exists("X-Correlation-Id");
    }

    @Test
    void deveBloquearAcessoAdminSemChave() {
        webTestClient.get()
            .uri("/internal/status")
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().exists("X-Blocked-Reason");
    }

    @Test
    void devePermitirAcessoAdminComChave() {
        webTestClient.get()
            .uri("/internal/status")
            .header("X-Admin-Key", "test-admin-key")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("admin-ok");
    }

    @Test
    void deveBloquearTentativaDeInjecaoSQL() {
        webTestClient.get()
            .uri("/api/dados?consulta=SELECT * FROM usuarios")
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().valueEquals("X-Blocked-Reason", "Padrão de injeção SQL detectado");
    }
    
    @Test
    void deveAplicarCabecalhosDoCircuitBreaker() {
        webTestClient.get()
            .uri("/pix/test")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("X-Circuit-Breaker-State", "closed");
    }
    
    @Test
    void deveRotearDocumentacaoSwagger() {
        webTestClient.get()
            .uri("/v3/api-docs/pix")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.openapi").isEqualTo("3.0.1");
    }
    
    @Test
    void deveBloquearTentativaPathTraversal() {
        webTestClient.get()
            .uri("/pix/..%2Fsegredo")
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().valueEquals("X-Blocked-Reason", "Tentativa de Path Traversal detectada");
    }
}