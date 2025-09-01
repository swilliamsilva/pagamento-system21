package test.auth_service;

import com.pagamento.auth.controller.AuthController;
import com.pagamento.auth.service.AuthService;
import com.pagamento.common.request.AuthRequest;
import com.pagamento.common.response.AuthResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    private AuthController controller;
    private AuthService service;

    @BeforeEach
    void setup() {
        service = Mockito.mock(AuthService.class);
        controller = new AuthController(service);
    }

    @Test
    void deveAutenticarComSucesso() {
        AuthRequest request = AuthRequest.builder()
                .username("will")
                .password("123")
                .build();

        AuthResponse responseMock = AuthResponse.builder()
                .token("token.jwt")
                .build();

        Mockito.when(service.autenticar(Mockito.any(AuthRequest.class)))
                .thenReturn(responseMock);

        ResponseEntity<AuthResponse> response = controller.autenticar(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("token.jwt", response.getBody().getToken());
    }
}
