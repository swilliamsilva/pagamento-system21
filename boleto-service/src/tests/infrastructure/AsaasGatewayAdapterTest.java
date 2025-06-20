package tests.boletoservice.infrastructure;

import com.pagamento.boleto.infrastructure.adapters.gateway.AsaasGatewayAdapter;
import org.junit.jupiter.api.*;

class AsaasGatewayAdapterTest {

    private AsaasGatewayAdapter adapter;

    @BeforeEach
    void setup() {
        adapter = new AsaasGatewayAdapter(); // mock real pode ser necessário
    }

    @Test
    void deveEnviarBoletoComSucesso() {
        // Simulação - quando houver dependência externa, mockar HTTP client
        Assertions.assertDoesNotThrow(() -> adapter.enviarBoleto("123", 100.0));
    }
}
