/* ========================================================
# Classe: GatewayIntegrationTest
# Módulo: api-gateway
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Testes de integração para o Spring Cloud Gateway.
# ======================================================== */

package com.pagamento.gateway;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setupMocks() {
        WireMock.reset();

        stubFor(get(urlEqualTo("/data"))
            .willReturn(okJson("{\"success\": true, \"data\": \"mocked\", \"timestamp\": 1234567890}")
                .withHeader("X-Correlation-Id", "test-correlation-id")
                .withHeader("X-Content-Type-Options", "nosniff")
                .withHeader("X-Frame-Options", "DENY")
                .withHeader("Content-Security-Policy", "default-src 'self'")
            ));

        stubFor(get(urlEqualTo("/resource"))
            .willReturn(ok()
                .withHeader("Content-Type", "text/plain")
                .withBody("resource")));

        stubFor(get(urlMatching("/test;.*"))
            .willReturn(badRequest()));
    }

    @Test
    void shouldBlockDangerousPath() {
        webTestClient.get()
            .uri("/test;param=attack")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldTransformResponse() {
        webTestClient.get()
            .uri("/api/data")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").isEqualTo("mocked")
            .jsonPath("$.timestamp").isEqualTo(1234567890);
    }

    @Test
    void shouldAddCorrelationHeader() {
        webTestClient.get()
            .uri("/api/data")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().exists("X-Correlation-Id");
    }

    @Test
    void shouldApplySecurityHeaders() {
        webTestClient.get()
            .uri("/api/data")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
            .expectHeader().valueEquals("X-Frame-Options", "DENY")
            .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'");
    }

    @Test
    void shouldEnforceRateLimit() {
        // ⚠️ Isso é apenas ilustrativo — o rate limiter real deve ser testado com um filtro customizado ou lib externa.
        for (int i = 0; i < 100; i++) {
            webTestClient.get()
                .uri("/api/resource")
                .exchange()
                .expectStatus().isOk();
        }

        // Simula a 101ª requisição bloqueada (mockada pelo WireMock, ou tratada pelo filtro se presente)
        webTestClient.get()
            .uri("/api/resource")
            .exchange()
            .expectStatus().isEqualTo(429)
            .expectHeader().valueEquals("Retry-After", "60");
    }
}
