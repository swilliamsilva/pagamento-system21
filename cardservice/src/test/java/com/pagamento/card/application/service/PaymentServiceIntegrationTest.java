package com.pagamento.card.application.service;

import com.pagamento.card.application.dto.*;
import com.pagamento.card.domain.strategy.BandeiraStrategy;
import com.pagamento.core.common.resilience.ResilienceManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceIntegrationTest {
    
    @Mock
    private ApplicationContext context;
    
    @Mock
    private BandeiraStrategy visaStrategy;
    
    @Mock
    private BandeiraStrategy fallbackStrategy;
    
    @Mock
    private PaymentMetrics paymentMetrics;
    
    @Mock
    private AuditService auditService;

    @Test
    void shouldProcessVisaPaymentSuccessfully() {
        // Configuração
        PaymentService service = new PaymentService(
            context, fallbackStrategy, paymentMetrics, auditService
        );
        
        CardRequestDTO request = new CardRequestDTO();
        request.setBandeira("VISA");
        request.setValor(BigDecimal.valueOf(100.00));
        
        CardResponseDTO expectedResponse = new CardResponseDTO(
            "TXN-123", "VISA", PaymentStatus.APPROVED, 
            BigDecimal.valueOf(100.00), "AUTH-123", "Sucesso"
        );
        
        when(context.containsBean("VISA")).thenReturn(true);
        when(context.getBean("VISA", BandeiraStrategy.class)).thenReturn(visaStrategy);
        when(visaStrategy.processarPagamento(request)).thenReturn(expectedResponse);
        
        // Execução
        CardResponseDTO response = service.processarPagamento(request);
        
        // Verificação
        assertEquals("VISA", response.getBandeira());
        assertEquals(PaymentStatus.APPROVED, response.getStatus());
        verify(auditService).saveTransaction(expectedResponse);
    }

    @Test
    void shouldUseFallbackWhenCircuitOpen() {
        // Configuração
        ResilienceManager.forceOpenCircuit("payment-circuit-VISA");
        
        PaymentService service = new PaymentService(
            context, fallbackStrategy, paymentMetrics, auditService
        );
        
        CardRequestDTO request = new CardRequestDTO();
        request.setBandeira("VISA");
        request.setValor(BigDecimal.valueOf(100.00));
        
        CardResponseDTO fallbackResponse = new CardResponseDTO(
            "TXN-FALLBACK", "VISA", PaymentStatus.PROCESSING_ERROR, 
            BigDecimal.valueOf(100.00), null, "Fallback"
        );
        
        when(fallbackStrategy.processarPagamento(request)).thenReturn(fallbackResponse);
        
        // Execução
        CardResponseDTO response = service.processarPagamento(request);
        
        // Verificação
        assertEquals(PaymentStatus.PROCESSING_ERROR, response.getStatus());
        verify(paymentMetrics).incrementError("VISA", "CircuitBreakerOpen");
        
        // Reset para outros testes
        ResilienceManager.resetCircuit("payment-circuit-VISA");
    }
}