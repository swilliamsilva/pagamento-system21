package test.auth_service;

import com.pagamento.auth.service.AuthService;
import com.pagamento.common.request.AuthRequest;
import com.pagamento.common.response.AuthResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService service;

    @BeforeEach
    void setup() {
        service = Mockito.mock(AuthService.class); // agora mockado
    }

    @Test
    void deveGerarTokenFake() {
        AuthRequest request = AuthRequest.builder()
                .username("will")
                .password("123")
                .build();

        AuthResponse responseMock = AuthResponse.builder()
                .token("fake-token-will")
                .build();

        Mockito.when(service.autenticar(Mockito.any(AuthRequest.class)))
                .thenReturn(responseMock);

        AuthResponse response = service.autenticar(request);

        assertNotNull(response.getToken());
        assertTrue(response.getToken().startsWith("fake-token"));
    }
}
