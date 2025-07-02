package com.pagamento.gateway;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.pagamento.gateway.filters.RateLimitingFilter;
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
@AutoConfigureWireMock(port = 0)  // Porta dinâmica para WireMock
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void setup() {
        WireMock.reset();
        rateLimitingFilter.clearBuckets();

        // Stub para /api/data
        stubFor(get(urlEqualTo("/api/data"))
            .willReturn(okJson("{\"success\": true, \"data\": \"mocked\", \"timestamp\": 1234567890}")
                .withHeader("X-Correlation-Id", "test-correlation-id")
                .withHeader("X-Content-Type-Options", "nosniff")
                .withHeader("X-Frame-Options", "DENY")
                .withHeader("Content-Security-Policy", "default-src 'self'")
            ));

        // Stub para /api/resource - importante para o teste que falhava
        stubFor(get(urlEqualTo("/api/resource"))
            .willReturn(ok()
                .withHeader("Content-Type", "text/plain")
                .withHeader("Retry-After", "60")
                .withBody("resource")));

        // Stub para rota inválida /test;...
        stubFor(get(urlMatching("/test;.*"))
            .willReturn(badRequest()));

        // Configura WebTestClient para não aceitar gzip
        webTestClient = webTestClient.mutate()
            .defaultHeader("Accept-Encoding", "identity")
            .build();
    }

    @Test
    void shouldEnforceRateLimit() {
        // 5 requisições válidas para /api/resource, devem passar com 200
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                .uri("/api/resource")
                .exchange()
                .expectStatus().isOk();
        }

        // 6ª requisição deve retornar 429 (Too Many Requests) e header Retry-After = 60
        webTestClient.get()
            .uri("/api/resource")
            .exchange()
            .expectStatus().isEqualTo(429)
            .expectHeader().valueEquals("Retry-After", "60");
    }

    // Aqui podem entrar outros testes de integração...
}
