package com.pagamento.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Value;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(
    classes = GatewayApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class GatewayRouteTest {

    @LocalServerPort
    private int gatewayPort;

    @Value("${wiremock.server.port}")
    private int wiremockPort;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRouteToPixService() {
        stubFor(get(urlPathEqualTo("/test"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("PIX Service OK")));

        webTestClient.get()
            .uri("http://localhost:" + gatewayPort + "/pix/test")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("PIX Service OK");
    }
}
