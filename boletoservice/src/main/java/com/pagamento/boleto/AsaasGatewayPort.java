package com.pagamento.boleto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.model.PagamentoStatus;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AsaasGatewayAdapterTest {

    @Autowired
    private AsaasGatewayPort asaasGatewayPort;

    @MockBean
    private AsaasClient asaasClient; // Cliente real que faz as chamadas HTTP

    // Teste para verificar injeção de dependência
    @Test
    void contextLoads() {
        assertThat(asaasGatewayPort).isNotNull();
    }

    // Testes para envio de boleto
    @Test
    void deveEnviarBoletoComSucesso() {
        when(asaasClient.criarCobranca(any())).thenReturn("pay_123456789");
        
        String paymentId = asaasGatewayPort.enviarBoleto("123", BigDecimal.valueOf(100.00));
        
        assertNotNull(paymentId);
        assertEquals("pay_123456789", paymentId);
    }

    @Test
    void deveLancarExcecaoAoEnviarBoleto() {
        when(asaasClient.criarCobranca(any())).thenThrow(new RuntimeException("Falha na API"));
        
        assertThrows(RuntimeException.class, () -> 
            asaasGatewayPort.enviarBoleto("123", BigDecimal.valueOf(100.00))
        );
    }

    // Testes para consulta de boleto
    @Test
    void deveConsultarBoletoComSucesso() {
        BoletoStatus statusMock = new BoletoStatus("PENDING", "2023-12-31");
        when(asaasClient.consultarCobranca(anyString())).thenReturn(statusMock);
        
        BoletoStatus status = asaasGatewayPort.consultarBoleto("pay_123456789");
        
        assertNotNull(status);
        assertEquals("PENDING", status.getStatus());
    }

    @Test
    void deveRetornarNullAoConsultarBoletoInexistente() {
        when(asaasClient.consultarCobranca(anyString())).thenReturn(null);
        
        BoletoStatus status = asaasGatewayPort.consultarBoleto("id_inexistente");
        
        assertNull(status);
    }

    // Testes para cancelamento de boleto
    @Test
    void deveCancelarBoletoComSucesso() {
        doNothing().when(asaasClient).cancelarCobranca(anyString());
        
        assertDoesNotThrow(() -> 
            asaasGatewayPort.cancelarBoleto("pay_123456789")
        );
    }

    @Test
    void deveLancarExcecaoAoCancelarBoletoInexistente() {
        doThrow(new RuntimeException("Cobrança não encontrada"))
            .when(asaasClient).cancelarCobranca(anyString());
        
        assertThrows(RuntimeException.class, () -> 
            asaasGatewayPort.cancelarBoleto("id_inexistente")
        );
    }

    // Testes para confirmação de pagamento
    @Test
    void deveConfirmarPagamentoComSucesso() {
        when(asaasClient.confirmarPagamento(anyString()))
            .thenReturn(new PagamentoStatus("CONFIRMED", "2023-10-15T10:30:00Z"));
        
        PagamentoStatus status = asaasGatewayPort.confirmarPagamento("pay_123456789");
        
        assertNotNull(status);
        assertEquals("CONFIRMED", status.getStatus());
    }

    @Test
    void deveRetornarStatusPendenteAoConfirmarPagamento() {
        when(asaasClient.confirmarPagamento(anyString()))
            .thenReturn(new PagamentoStatus("PENDING", null));
        
        PagamentoStatus status = asaasGatewayPort.confirmarPagamento("pay_123456789");
        
        assertNotNull(status);
        assertEquals("PENDING", status.getStatus());
        assertNull(status.getDataConfirmacao());
    }
}