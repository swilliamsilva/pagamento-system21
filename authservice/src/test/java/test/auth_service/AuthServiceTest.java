package test.auth_service;

import com.pagamento.auth.service.AuthService;
import com.pagamento.common.request.AuthRequest;
import com.pagamento.common.response.AuthResponse;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService service;

    @BeforeEach
    void setup() {
        service = new AuthService(null, null, null); // Simples, sem dependências
    }

    @Test
    void deveGerarTokenFake() {
        AuthRequest request = new AuthRequest("will", "123");
        AuthResponse response = service.autenticar(request);

        assertNotNull(response.token());
        assertTrue(response.token().startsWith("fake-token"));
    }
}
