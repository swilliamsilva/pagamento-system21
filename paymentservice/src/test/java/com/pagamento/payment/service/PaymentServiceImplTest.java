package com.pagamento.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import com.pagamento.common.mapper.PaymentMapper;
import com.pagamento.common.messaging.PaymentEvent;
import com.pagamento.common.model.Payment;
import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
import com.pagamento.payment.dto.mapper.PaymentMapper;
import com.pagamento.payment.enums.PaymentType;
import com.pagamento.payment.port.output.PaymentRepositoryPort;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    
    @Mock private PaymentRepositoryPort repository;
    @Mock private PaymentMapper mapper;
    @Mock private KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    @Mock private RestTemplate restTemplate;
    
    @InjectMocks
    private PaymentServiceImpl service;
    
    @BeforeEach
    void setup() {
        // Configura o valor do tópico para o teste
        service.paymentProcessedTopic = "pagamento-processado";
    }
    
    @Test
    void shouldProcessPixPayment() {
        PaymentRequest request = new PaymentRequest("user1", "PIX", new BigDecimal("100"));
        Payment payment = new Payment();
        payment.setTipoPagamento(PaymentType.PIX); // Adicionado tipo de pagamento
        
        when(mapper.toEntity(request, PaymentType.PIX)).thenReturn(payment);
        when(repository.salvar(payment)).thenReturn(payment);
        
        service.processarPagamento(request);
        
        verify(restTemplate).postForEntity(eq("http://pix-service/api/pix"), eq(request), eq(Void.class));
        verify(kafkaTemplate).send(eq("pagamento-processado"), any(PaymentEvent.class));
    }
    
    @Test
    void shouldTriggerFallbackOnFailure() {
        PaymentRequest request = new PaymentRequest("user1", "PIX", new BigDecimal("100"));
        
        when(mapper.toEntity(request, PaymentType.PIX)).thenThrow(new RuntimeException("Simulated failure"));
        
        PaymentResponse response = service.processarPagamento(request);
        
        assertEquals("FALHA", response.status());
        assertEquals("Serviço temporariamente indisponível", response.mensagem());
        assertEquals(new BigDecimal("100"), response.valor());
        assertEquals("PIX", response.tipoPagamento());
    }
}