package com.pagamento.gateway.fallback;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FallbackControllerTest {

    @InjectMocks
    private FallbackController fallbackController;

    @Test
    void serviceFallback_shouldReturnCorrectResponseForPixService() {
        ResponseEntity<Map<String, Object>> response = fallbackController.serviceFallback("pixservice");

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(503, body.get("status"));
        assertEquals("Service Unavailable", body.get("error"));
        assertEquals("Serviço de Controle de Pix está indisponível no momento. Tente novamente mais tarde.", body.get("message"));
        assertEquals("pixservice", body.get("service"));
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("recovery_estimate"));
    }


    @Test
    void serviceFallback_shouldReturnCorrectResponseForUnknownService() {
        // Act
        ResponseEntity<Map<String, Object>> response = fallbackController.serviceFallback("unknown-service");

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(503, body.get("status"));
        assertEquals("Service Unavailable", body.get("error"));
        assertEquals("Serviço está indisponível no momento. Tente novamente mais tarde.", body.get("message"));
        assertEquals("unknown-service", body.get("service"));
        assertEquals("30", response.getHeaders().getFirst("Retry-After"));
    }

    @Test
    void serviceFallback_shouldHandleAllDefinedServices() {
        // Arrange
        String[] services = {"pix", "auth", "assasintegration", "authservice", 
                             "boletoservice", "cardservice", "cloudaws", 
                             "common", "paymentservice", "pixservice"};
        
        // Act & Assert
        for (String service : services) {
            ResponseEntity<Map<String, Object>> response = fallbackController.serviceFallback(service);
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().get("message").toString().contains("indisponível"));
        }
    }
}