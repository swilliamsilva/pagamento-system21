package com.pagamento.boleto;

import com.pagamento.boleto.domain.service.BoletoService;
import com.pagamento.common.dto.BoletoRequestDTO;
import org.junit.jupiter.api.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

@ActiveProfiles("test")
class BoletoServiceTest {

    private BoletoService service;

    @BeforeEach
    void setup() {
        service = new BoletoService(null, null, null, null, null, null);
    }

    @Test
    void deveGerarCodigoDeBarrasValido() {
        BoletoRequestDTO request = new BoletoRequestDTO("cliente1",BigDecimal.valueOf(200.0), "2025-07-01");
        String codigoBarras = service.gerarBoleto(request);
        assertNotNull(codigoBarras);
        assertTrue(codigoBarras.length() >= 40);
    }
}
