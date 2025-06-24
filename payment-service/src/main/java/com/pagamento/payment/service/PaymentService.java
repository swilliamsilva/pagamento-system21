package com.pagamento.payment.service;

import com.pagamento.common.messaging.PaymentEvent;
import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
import com.pagamento.payment.dto.mapper.PaymentMapper;
import com.pagamento.payment.model.Payment;
import com.pagamento.payment.port.output.PaymentRepositoryPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepositoryPort repository;
    private final PaymentMapper mapper;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final RestTemplate restTemplate;

    public PaymentService(
        PaymentRepositoryPort repository,
        PaymentMapper mapper,
        KafkaTemplate<String, PaymentEvent> kafkaTemplate,
        RestTemplate restTemplate
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    @Transactional
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackProcessarPagamento")
    public PaymentResponse processarPagamento(PaymentRequest request) {
        Payment payment = mapper.toEntity(request);
        Payment savedPayment = repository.salvar(payment);
        
        try {
            if("PIX".equals(request.tipo())) {
                restTemplate.postForEntity("http://pix-service/api/pix", request, Void.class);
            } else if("BOLETO".equals(request.tipo())) {
                restTemplate.postForEntity("http://boleto-service/api/boleto", request, Void.class);
            }
        } catch (Exception e) {
            log.error("Falha ao integrar com serviço de {}", request.tipo(), e);
            throw new RuntimeException("Falha na integração com gateway de pagamento");
        }
        
        // Corrigido: Usando novo padrão de construção
        PaymentEvent event = new PaymentEvent(
            savedPayment.getId(),
            savedPayment.getTipo(),
            savedPayment.getValor(),
            Instant.now()
        );
                
        kafkaTemplate.send("pagamento-processado", event);
        
        return mapper.toResponse(savedPayment);
    }

    private PaymentResponse fallbackProcessarPagamento(PaymentRequest request, Throwable t) {
        log.error("Fallback ativado para pagamento: {}", request.tipo(), t);
        
        // Corrigido: Usando novo padrão de construção
        return new PaymentResponse(
            "Serviço temporariamente indisponível", 
            null, 
            null, 
            null
        );
    }
}