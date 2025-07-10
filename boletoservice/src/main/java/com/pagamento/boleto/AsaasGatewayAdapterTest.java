package com.pagamento.boleto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.model.PagamentoStatus;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;
import com.pagamento.boleto.infra.adapters.AsaasGatewayAdapter;
import com.pagamento.boleto.infra.clients.AsaasClient;
import com.pagamento.boleto.infra.clients.AsaasStatusResponse;

@ExtendWith(MockitoExtension.class)
class AsaasGatewayAdapterTest {

    private static final String MOCK_PAYMENT_ID = "pay_123456789";

    @Mock
    private AsaasClient asaasClient;

    @InjectMocks
    private AsaasGatewayAdapter asaasGatewayAdapter;

    @Test
    void deveEnviarBoletoComSucesso() {
        when(asaasClient.criarCobranca(any(), any())).thenReturn(MOCK_PAYMENT_ID);
        
        String paymentId = asaasGatewayAdapter.enviarBoleto("123", BigDecimal.valueOf(100.00));
        
        assertNotNull(paymentId);
        assertEquals(MOCK_PAYMENT_ID, paymentId);
    }

    @Test
    void deveConsultarBoletoComSucesso() {
        AsaasStatusResponse response = new AsaasStatusResponse("PENDING", "2023-12-31");
        when(asaasClient.consultarCobranca(anyString())).thenReturn(response);
        
        BoletoStatus status = asaasGatewayAdapter.consultarBoleto(MOCK_PAYMENT_ID);
        
        assertNotNull(status);
        assertEquals(BoletoStatus.EMITIDO, status);
    }

    @Test
    void deveRetornarNullAoConsultarBoletoInexistente() {
        when(asaasClient.consultarCobranca(anyString())).thenReturn(null);
        
        BoletoStatus status = asaasGatewayAdapter.consultarBoleto("id_inexistente");
        
        assertNull(status);
    }

    @Test
    void deveMapearStatusCorretamente() {
        AsaasGatewayAdapter adapter = new AsaasGatewayAdapter(asaasClient);
        
        assertEquals(BoletoStatus.EMITIDO, adapter.mapAsaasStatusToDomain("PENDING"));
        assertEquals(BoletoStatus.PAGO, adapter.mapAsaasStatusToDomain("RECEIVED"));
        assertEquals(BoletoStatus.VENCIDO, adapter.mapAsaasStatusToDomain("OVERDUE"));
        assertEquals(BoletoStatus.CANCELADO, adapter.mapAsaasStatusToDomain("CANCELLED"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            adapter.mapAsaasStatusToDomain("INVALID_STATUS")
        );
    }

    @Test
    void deveConfirmarPagamentoComSucesso() {
        AsaasStatusResponse response = new AsaasStatusResponse("CONFIRMED", null, "2023-10-15T10:30:00Z");
        when(asaasClient.confirmarPagamento(anyString())).thenReturn(response);
        
        PagamentoStatus status = asaasGatewayAdapter.confirmarPagamento(MOCK_PAYMENT_ID);
        
        assertNotNull(status);
        assertEquals("CONFIRMED", status.getStatus());
        assertEquals("2023-10-15T10:30:00Z", status.getDataConfirmacao());
    }
}