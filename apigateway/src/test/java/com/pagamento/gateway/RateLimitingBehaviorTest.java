package com.pagamento.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.client.WireMock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test-rate-limit")  // Perfil corrigido
@TestPropertySource(properties = {
    "filters.local-rate-limit.enabled=true",
    "filters.rate-limiting.capacity=5",
    "filters.rate-limiting.refill-tokens=5",
    "filters.rate-limiting.refill-period=60"
})
class RateLimitingBehaviorTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void configurarStubs() {
        WireMock.reset();

        stubFor(get(urlEqualTo("/api/limited"))
            .willReturn(ok()
                .withHeader("Content-Type", "text/plain")
                .withBody("ok")));
    }
    
    @Test
    void devePermitirRequisicoesDentroDoLimite() {
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                .uri("/api/limited")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Rate-Limit-Remaining")
                .expectHeader().valueEquals("X-Rate-Limit-Capacity", "5");
        }
    }

    @Test
    void deveBloquearRequisicoesAcimaDoLimite() {
        // Primeiro: fazer 5 requisições (dentro do limite)
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                .uri("/api/limited")
                .exchange()
                .expectStatus().isOk();
        }

        // Sexta requisição: deve ser bloqueada
        webTestClient.get()
            .uri("/api/limited")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
            .expectHeader().exists("Retry-After")
            .expectHeader().valueEquals("X-Rate-Limit-Remaining", "0")
            .expectHeader().exists("X-Rate-Limit-Reset");
    }

    @Test
    void deveIncluirCabecalhosDeLimiteEmTodasAsRespostas() {
        webTestClient.get()
            .uri("/api/limited")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().exists("X-Rate-Limit-Remaining")
            .expectHeader().valueEquals("X-Rate-Limit-Capacity", "5")
            .expectHeader().exists("X-Rate-Limit-Reset");
    }
    
    @Test
    void deveDistinguirClientesPorIp() {
        // Primeiro cliente (IP 192.168.1.1)
        WebTestClient client1 = webTestClient.mutate()
            .defaultHeader("X-Forwarded-For", "192.168.1.1")
            .build();
        
        // Segundo cliente (IP 192.168.1.2)
        WebTestClient client2 = webTestClient.mutate()
            .defaultHeader("X-Forwarded-For", "192.168.1.2")
            .build();

        // Cliente 1 usa todo seu limite
        for (int i = 0; i < 5; i++) {
            client1.get()
                .uri("/api/limited")
                .exchange()
                .expectStatus().isOk();
        }
        
        // Cliente 1 deve ser bloqueado
        client1.get()
            .uri("/api/limited")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        
        // Cliente 2 deve conseguir acessar
        client2.get()
            .uri("/api/limited")
            .exchange()
            .expectStatus().isOk();
    }
}