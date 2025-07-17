package com.pagamento.pix.application.service;

import com.pagamento.common.payment.PaymentOrchestratorPort;
import com.pagamento.common.payment.TransactionResponse;
import com.pagamento.common.payment.Status;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.ports.PixRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PixServiceImplTest {

    @Mock
    private PaymentOrchestratorPort paymentOrchestrator;
    
    @Mock
    private PixRepositoryPort pixRepository;
    
    @InjectMocks
    private PixServiceImpl pixService;

    @Test
    void shouldProcessValidPix() {
        Pix pix = new Pix();
        pix.setChaveOrigem("chave1");
        pix.setChaveDestino("chave2");
        pix.setValor(BigDecimal.TEN);
        
        when(paymentOrchestrator.orchestrate(any()))
            .thenReturn(new TransactionResponse("tx123", Status.PROCESSED));
        when(pixRepository.salvar(any())).thenReturn(pix);
        
        Pix result = pixService.processarPix(pix);
        
        assertNotNull(result);
        assertEquals("PROCESSED", result.getStatus());
        verify(paymentOrchestrator, times(1)).orchestrate(pix);
    }

    @Test
    void shouldRejectInvalidPix() {
        Pix invalidPix = new Pix(); // Dados inválidos
        assertThrows(PixValidationException.class, () -> pixService.processarPix(invalidPix));
    }
    
    @Test
    void processarPix_deveRetornarErroQuandoBacenIndisponivel() {
        when(bacenPort.registrarPix(any())).thenThrow(BacenIntegrationException.class);
        
        assertThrows(ServiceException.class, () -> service.processarPix(requestDTO));
        verify(repositoryPort, never()).salvar(any());
    }
    
    @Test
    void processarPix_deveRejeitarTransacaoDuplicada() {
        when(repositoryPort.buscarPorIdempotencyKey(any())).thenReturn(Optional.of(new Pix()));
        assertThrows(DuplicateTransactionException.class, () -> service.processarPix(requestDTO));
    }
}