package tests.authservice;

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
        AuthRequest request = new AuthRequest("will", "123");
        AuthResponse responseMock = new AuthResponse("token.jwt");

        Mockito.when(service.autenticar(request)).thenReturn(responseMock);

        ResponseEntity<AuthResponse> response = controller.autenticar(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token.jwt", response.getBody().token());
    }
}
