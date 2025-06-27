package com.pagamento.boleto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;

@SpringBootTest
public class AsaasGatewayAdapterTest {

    @Autowired
    private AsaasGatewayPort asaasGatewayPort;

    @Test
    void contextLoads() {
        assertThat(asaasGatewayPort).isNotNull();
    }
    
    @Test
    void deveRegistrarBoletoComSucesso() {
        Boleto boleto = new Boleto();
        boleto.setCodigo("123");
        boleto.setValor(100.00);
        
        assertDoesNotThrow(() -> 
            asaasGatewayPort.registrarBoleto(boleto)
        );
    }
}