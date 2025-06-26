package com.pagamento.boleto;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.pagamento.boleto.infrastructure.adapters.gateway.AsaasGatewayAdapter;

@SpringBootTest
@ActiveProfiles("test")
class AsaasGatewayAdapterTest {

    @Autowired
    private AsaasGatewayAdapter adapter;

    @Test
    void deveEnviarBoletoComSucesso() {
        assertDoesNotThrow(() -> adapter.enviarBoleto("123",BigDecimal(100.0)));
    }

	private BigDecimal BigDecimal(double d) {
		// TODO Auto-generated method stub
		return null;
	}
}
