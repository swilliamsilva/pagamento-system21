package com.pagamento.pix.infrastructure.adapters;

import com.pagamento.pix.domain.model.ChavePix;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.infrastructure.adapters.output.PaymentRequest;
import com.pagamento.pix.infrastructure.adapters.output.PaymentServiceAdapter;
import com.pagamento.pix.infrastructure.adapters.output.TransactionResponse;
import com.pagamento.pix.infrastructure.clients.PaymentServiceClient;
import com.pagamento.pix.infrastructure.integration.PaymentFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientException;

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
        // Criar request válido
        PaymentRequest request = new PaymentRequest(
            "origem@email.com",
            "destino@email.com",
            new BigDecimal("100.00")
        );
        
        // Configurar mock para lançar exceção
        when(client.processPayment(any(PaymentRequest.class)))
            .thenThrow(WebClientException.class);
        
        // Executar e verificar exceção
        assertThrows(PaymentFailedException.class, 
            () -> adapter.orchestrate(criarPix()));
        
        // Verificar que foi chamado 1 vez (com retry configurado seria mais)
        verify(client, times(1)).processPayment(any(PaymentRequest.class));
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