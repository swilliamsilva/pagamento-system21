package com.pagamento.pix.infrastructure.adapters;

import com.pagamento.pix.domain.model.ChavePix;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.infrastructure.adapters.output.PaymentServiceAdapter;
import com.pagamento.pix.infrastructure.clients.PaymentServiceClient;
import com.pagamento.pix.infrastructure.integration.PaymentFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException; // Alternativa mais comum

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceAdapterTest {

    @Mock
    private PaymentServiceClient client;

    @InjectMocks
    private PaymentServiceAdapter adapter;

    @Test
    void processarPagamento_deveRetornarErroAposTentativas() {
        // Usando ResourceAccessException como alternativa
        when(client.processPayment(any()))
            .thenThrow(new ResourceAccessException("Erro simulado de conexão"));
        
        assertThrows(PaymentFailedException.class, 
            () -> adapter.orchestrate(criarPix()));
        
        verify(client, times(3)).processPayment(any());
    }
    
    private Pix criarPix() {
        return Pix.builder()
            .id("PIX-001")
            .chaveOrigem(new ChavePix("origem@email.com"))
            .chaveDestino(new ChavePix("destino@email.com"))
            .valor(new BigDecimal("100.00"))
            .build();
    }
}