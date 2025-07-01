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
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0) // WireMock em porta aleatória
@ActiveProfiles("test")
class GatewayRouteIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WireMockServer wireMockServer; // injetado para manipular WireMock real

    @BeforeEach
    void setupStubs() {
        wireMockServer.resetAll(); // reseta stubs e mappings do WireMock real

        wireMockServer.stubFor(get(urlEqualTo("/pix/test"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBody("ok")));

        wireMockServer.stubFor(get(urlEqualTo("/fallback/pix"))
            .willReturn(aResponse()
                .withStatus(503)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"status\":503,\"error\":\"Service Unavailable\",\"service\":\"pix\"}")));

        wireMockServer.stubFor(get(urlEqualTo("/api/data"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withHeader("Content-Encoding", "identity") // evita erro de descompressão
                .withBody("{\"success\":true,\"status\":200,\"data\":{\"value\":\"mock\"},\"timestamp\":\"2025-07-01T00:00:00Z\"}")));

        wireMockServer.stubFor(get(urlEqualTo("/api/limited"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBody("ok")));
    }

    @Test
    void shouldRouteToPixService() {
        webTestClient.get()
            .uri("/pix/test")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().exists("X-Correlation-Id");
    }

    @Test
    void shouldTriggerFallbackWhenServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/pix")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
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
