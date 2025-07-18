package com.pagamento.boleto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.application.dto.BoletoResponseDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BoletoFlowIntegrationTest { // Removido modificador 'public'

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateBoletoSuccessfully() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente C",
            "Beneficiário D",
            BigDecimal.valueOf(150.00),
            LocalDate.now().plusDays(7),
            "DOC-456",
            "Instruções de pagamento",
            "Local de pagamento"
        );

        ResponseEntity<BoletoResponseDTO> response = restTemplate.postForEntity(
            "/api/boletos", // Endpoint corrigido
            request,
            BoletoResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BoletoResponseDTO boleto = response.getBody();
        assertNotNull(boleto);
        assertNotNull(boleto.getId());
        assertEquals("Cliente C", boleto.getPagador());
        assertEquals(BigDecimal.valueOf(150.00), boleto.getValor());
    }

    @Test
    void shouldGetBoletoSuccessfully() {
        // Primeiro cria um boleto
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente E",
            "Beneficiário F",
            BigDecimal.valueOf(200.00),
            LocalDate.now().plusDays(14),
            "DOC-789",
            "Mais instruções",
            "Outro local"
        );
        
        ResponseEntity<BoletoResponseDTO> createResponse = restTemplate.postForEntity(
            "/api/boletos",
            request,
            BoletoResponseDTO.class
        );
        String boletoId = createResponse.getBody().getId();

        // Agora busca o boleto criado
        ResponseEntity<BoletoResponseDTO> getResponse = restTemplate.getForEntity(
            "/api/boletos/" + boletoId,
            BoletoResponseDTO.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        BoletoResponseDTO boleto = getResponse.getBody();
        assertNotNull(boleto);
        assertEquals(boletoId, boleto.getId());
        assertEquals("Cliente E", boleto.getPagador());
    }
    
    @Test
    void shouldCancelBoletoSuccessfully() {
        // Cria um boleto
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente G",
            "Beneficiário H",
            BigDecimal.valueOf(300.00),
            LocalDate.now().plusDays(21),
            "DOC-101",
            "Cancelamento",
            "Local X"
        );
        
        ResponseEntity<BoletoResponseDTO> createResponse = restTemplate.postForEntity(
            "/api/boletos",
            request,
            BoletoResponseDTO.class
        );
        String boletoId = createResponse.getBody().getId();

        // Cancela o boleto
        ResponseEntity<Void> cancelResponse = restTemplate.exchange(
            "/api/boletos/" + boletoId + "/cancelar?motivo=Cancelamento solicitado",
            HttpMethod.POST,
            HttpEntity.EMPTY,
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, cancelResponse.getStatusCode());
    }
}