package com.pagamento.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GatewayIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldBlockDangerousPath() {
        webTestClient.get()
            .uri("/test;param=attack")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldEnforceRateLimit() {
        // Executa 100 requisições (limite)
        for (int i = 0; i < 100; i++) {
            webTestClient.get()
                .uri("/api/resource")
                .exchange()
                .expectStatus().isOk();
        }

        // 101ª deve falhar
        webTestClient.get()
            .uri("/api/resource")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SC_TOO_MANY_REQUESTS)
            .expectHeader().valueEquals("Retry-After", "60");
    }

    @Test
    void shouldTransformResponse() {
        webTestClient.get()
            .uri("/api/data")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").exists()
            .jsonPath("$.timestamp").isNumber();
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
}