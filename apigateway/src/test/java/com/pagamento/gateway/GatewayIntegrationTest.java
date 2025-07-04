package com.pagamento.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
/*
 * 
 * Resource	Date	Description
GatewayIntegrationTest.java	16 hours ago	Remove this unused import 'com.pagamento.gateway.filters.SecurityFilter'.

 * 
 * 
 * 
 * ***/


import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

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

import com.github.tomakehurst.wiremock.client.WireMock;
import com.pagamento.gateway.filters.RateLimitingFilter;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "filters.local-rate-limit.enabled=true",
    "filters.rate-limiting.capacity=5",
    "filters.rate-limiting.refill-tokens=5",
    "filters.rate-limiting.refill-period=60",
    "security.admin-key=test-admin-key"
})
class GatewayIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void configurar() {
        WireMock.reset();
        rateLimitingFilter.clearBuckets();

        // Configuração REALISTA do mock
        stubFor(get(urlEqualTo("/api/resource"))
            .willReturn(ok()
                .withHeader("Content-Type", "text/plain")
                .withBody("recurso")));

        stubFor(get(urlEqualTo("/internal/status"))
            .willReturn(ok().withBody("admin-ok")));

        // Configura fallback para teste
        stubFor(get(urlEqualTo("/fallback/pix"))
            .willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"message\":\"Fallback acionado\"}")));

        webTestClient = webTestClient.mutate()
            .defaultHeader("X-API-Key", "test-key") // Chave fixa para testes
            .defaultHeader("X-Forwarded-For", "192.168.1.1") // IP fixo para testes
            .defaultHeader("Accept-Encoding", "identity")
            .build();
    }

    @Test
    void deveAplicarLimiteDeTaxa() {
        // Requisições dentro do limite
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                .uri("/api/resource")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Rate-Limit-Remaining");
        }

        // Requisição que excede o limite
        webTestClient.get()
            .uri("/api/resource")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS) // 429
            .expectHeader().valueEquals("X-Rate-Limit-Remaining", "0")
            .expectHeader().exists("X-Rate-Limit-Reset")
            .expectHeader().exists("Retry-After");
    }

    @Test
    void deveIncluirCabecalhosDeLimiteDeTaxa() {
        webTestClient.get()
            .uri("/api/resource")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueMatches("X-Rate-Limit-Remaining", "\\d+")
            .expectHeader().valueEquals("X-Rate-Limit-Capacity", "5")
            .expectHeader().valueMatches("X-Rate-Limit-Reset", "\\d+");
    }

    @Test
    void deveBloquearTentativaDePathTraversal() {
        webTestClient.get()
            .uri("/test;../secret")
            .exchange()
            .expectStatus().isForbidden() // 403
            .expectHeader().valueEquals("X-Blocked-Reason", "Tentativa de Path Traversal detectada");
    }
    
    @Test
    void deveBloquearTentativaDeInjecaoSQL() {
        webTestClient.get()
            .uri("/api/data?query=SELECT * FROM users")
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().valueEquals("X-Blocked-Reason", "Padrão de injeção SQL detectado");
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
    void deveBloquearAcessoAdminSemChave() {
        webTestClient.get()
            .uri("/internal/status")
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().exists("X-Blocked-Reason");
    }
    
    @Test
    void deveAplicarCabecalhosDoCircuitBreaker() {
        webTestClient.get()
            .uri("/api/resource")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("X-Circuit-Breaker-State", "closed");
    }
    
    @Test
    void deveRotearRequisicoesParaSwagger() {
        // Configuração específica para Swagger
        stubFor(get(urlEqualTo("/v3/api-docs"))
            .willReturn(ok()
                .withBody("{\"openapi\":\"3.0.1\"}")));
        
        webTestClient.get()
            .uri("/v3/api-docs/pix")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.openapi").isEqualTo("3.0.1");
    }
    
    @Test
    void deveRetornarFallbackQuandoServicoIndisponivel() {
        // Simular falha no serviço
        stubFor(get(urlEqualTo("/pix/unavailable"))
            .willReturn(serverError()));
        
        webTestClient.get()
            .uri("/pix/unavailable")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE) // 503
            .expectBody()
            .jsonPath("$.message").isEqualTo("Fallback acionado");
    }
}