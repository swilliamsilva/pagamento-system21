package com.pagamento.auth.test.integration;

import com.pagamento.auth.dto.AuthRequestDTO;
import com.pagamento.auth.dto.AuthResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnJwtForValidLogin() {
        AuthRequestDTO login = new AuthRequestDTO("admin", "admin123");
        ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity("/auth/login", login, AuthResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }
}
