package com.pagamento.boleto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.application.dto.BoletoResponseDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BoletoFlowIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateBoletoSuccessfully() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente C",
            "111.222.333-44",
            BigDecimal.valueOf(150.00),
            LocalDate.now().plusDays(7)
        );

        ResponseEntity<BoletoResponseDTO> response = restTemplate.postForEntity(
            "/boletos",
            request,
            BoletoResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }
}