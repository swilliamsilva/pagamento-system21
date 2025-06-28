package com.pagamento.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GatewayRouteIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRouteToPixService() {
        webTestClient.get()
            .uri("/pix/test")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueMatches("X-Correlation-Id", ".+");
    }

    @Test
    void shouldTriggerFallbackWhenServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/pix")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)  // Corrigido
            .expectBody()
            .jsonPath("$.status").isEqualTo(503)
            .jsonPath("$.error").isEqualTo("Service Unavailable")
            .jsonPath("$.service").isEqualTo("pix");
    }

    @Test
    void shouldReturnCorrelationIdInResponse() {
        webTestClient.get()
            .uri("/pix/test")
            .header("X-Correlation-Id", "test-id-123")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("X-Correlation-Id", "test-id-123");
    }
    
    @Test
    void shouldHandleRateLimiting() {
        // Primeiras 100 requisições devem passar
        for (int i = 0; i < 100; i++) {
            webTestClient.get()
                .uri("/api/limited")
                .exchange()
                .expectStatus().isOk();
        }
        
        // 101ª requisição deve ser bloqueada
        webTestClient.get()
            .uri("/api/limited")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
            .expectHeader().valueEquals("Retry-After", "60");
    }
    
    @Test
    void shouldTransformResponseStructure() {
        webTestClient.get()
            .uri("/api/data")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.status").isEqualTo(200)
            .jsonPath("$.data").exists()
            .jsonPath("$.timestamp").exists();
    }
}