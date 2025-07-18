package com.pagamento.pix.domain.controller;

import org.junit.jupiter.api.Test;


import com.pagamento.pix.infrastructure.integration.BacenIntegrationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;



public class PixControllerIntegrationTest{





@Test
void aoEnviarPix_deveRetornarHeaderIdempotencyKey() {
    ResponseEntity<PixResponseDTO> response = restTemplate.postForEntity(...);
    assertNotNull(response.getHeaders().get("Idempotency-Key"));
}

@Test
void pixComScriptInjection_deveRetornar400() {
    PixRequestDTO request = new PixRequestDTO();
    request.setChave("<script>alert('XSS')</script>");
    assertThrows(HttpClientErrorException.BadRequest.class, () -> ...);
}
}