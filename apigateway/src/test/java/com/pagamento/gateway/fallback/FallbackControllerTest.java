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
        // Act
        ResponseEntity<Map<String, Object>> response = fallbackController.serviceFallback("pix");

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(503, body.get("status"));
        assertEquals("Service Unavailable", body.get("error"));
        assertEquals("Serviço de Pagamento Pix está indisponível no momento. Tente novamente mais tarde.", body.get("message"));
        assertEquals("pix", body.get("service"));
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("recovery_estimate"));
        assertTrue(Instant.parse(body.get("timestamp").toString()).isBefore(Instant.now()));
        assertTrue(Instant.parse(body.get("recovery_estimate").toString()).isAfter(Instant.now()));
        assertEquals("no-store, max-age=0", response.getHeaders().getCacheControl());
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