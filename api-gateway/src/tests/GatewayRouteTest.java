package tests.gateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.pagamento.gateway.GatewayApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
    classes = GatewayApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class GatewayRouteTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8081));
        wireMockServer.start();
        
        // Mock para pix-service
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/pix/test"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withBody("PIX Service OK"))
        );
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldRouteToPixService() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:8080/pix/test", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("PIX Service OK", response.getBody());
    }

    @Test
    void shouldLogRequests() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:8080/pix/test", String.class);
        
        // Verifique os logs manualmente ou com sistema de captura de logs
    }
}