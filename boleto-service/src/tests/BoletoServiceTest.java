package tests.boletoservice.domain;

import com.pagamento.boleto.domain.service.BoletoService;
import com.pagamento.common.dto.BoletoRequestDTO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class BoletoServiceTest {

    private BoletoService service;

    @BeforeEach
    void setup() {
        service = new BoletoService();
    }

    @Test
    void deveGerarCodigoDeBarrasValido() {
        BoletoRequestDTO request = new BoletoRequestDTO("cliente1", 200.0, "2025-07-01");
        String codigoBarras = service.gerarBoleto(request);
        assertNotNull(codigoBarras);
        assertTrue(codigoBarras.length() >= 40);
    }
}
