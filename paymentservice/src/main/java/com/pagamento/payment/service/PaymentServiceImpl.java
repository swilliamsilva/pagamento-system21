package com.pagamento.payment.service;

import com.pagamento.common.messaging.PaymentEvent;
import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
import com.pagamento.payment.dto.mapper.PaymentMapper;
import com.pagamento.payment.enums.PaymentType;
import com.pagamento.payment.model.Payment;
import com.pagamento.payment.port.output.PaymentRepositoryPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transaction;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepositoryPort repository;
    private final PaymentMapper mapper;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final RestTemplate restTemplate;
    
    @Value("${topic.name.payment-processed}")
    private String paymentProcessedTopic;
	public Object auditService;

    public PaymentServiceImpl(
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

    @Override
    @Transactional
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackProcessarPagamento")
    public PaymentResponse processarPagamento(PaymentRequest request) {
        // Converter string para enum
        PaymentType paymentType = PaymentType.valueOf(request.tipoPagamento());
        
        Payment payment = mapper.toEntity(request, paymentType);
        Payment savedPayment = repository.salvar(payment);
        
        // Contexto para logging estruturado
        Map<String, String> logContext = new HashMap<>();
        logContext.put("transactionId", savedPayment.getIdTransacao());
        logContext.put("paymentType", paymentType.name());
        logContext.put("amount", request.valor().toString());

        try {
            PaymentLogger.withContext(logContext, () -> {
                PaymentLogger.info("Iniciando processamento de pagamento");
                
                switch(paymentType) {
                    case PIX:
                        restTemplate.postForEntity("http://pix-service/api/pix", request, Void.class);
                        PaymentLogger.info("Integração PIX realizada com sucesso");
                        break;
                    case BOLETO:
                        restTemplate.postForEntity("http://boleto-service/api/boleto", request, Void.class);
                        PaymentLogger.info("Integração Boleto realizada com sucesso");
                        break;
                    case CARD:
                        restTemplate.postForEntity("http://card-service/api/card", request, Void.class);
                        PaymentLogger.info("Integração CARD realizada com sucesso");
                        break;
                    case ASAAS:
                        restTemplate.postForEntity("http://asaas-service/api/asaas", request, Void.class);
                        PaymentLogger.info("Integração Asaas realizada com sucesso");
                        break;                        
                        
                    default:
                        throw new IllegalArgumentException("Tipo de pagamento inválido");
                }
            });
        } catch (Exception e) {
            PaymentLogger.error(e, "Falha ao integrar com serviço de %s", paymentType);
            throw new PaymentProcessingException("Falha na integração com gateway de pagamento");
        }
        
        PaymentEvent event = new PaymentEvent(
            savedPayment.getIdTransacao(),
            savedPayment.getTipoPagamento().name(),
            savedPayment.getValor(),
            Instant.now()
        );
                
        kafkaTemplate.send(paymentProcessedTopic, event);
        
        PaymentLogger.info("Evento de pagamento enviado para o tópico: %s", paymentProcessedTopic);
        
        return mapper.toResponse(savedPayment);
    }

    private PaymentResponse fallbackProcessarPagamento(PaymentRequest request, Throwable t) {
        Map<String, String> logContext = new HashMap<>();
        logContext.put("paymentType", request.tipoPagamento());
        logContext.put("amount", request.valor().toString());
        
        PaymentLogger.withContext(logContext, () -> {
            PaymentLogger.error(t, "Fallback ativado para pagamento");
        });
        
        return new PaymentResponse(
            "FALHA",
            "Serviço temporariamente indisponível",
            request.valor(),
            request.tipoPagamento()
        );
    }
    
    public static class PaymentProcessingException extends RuntimeException {
        public PaymentProcessingException(String message) {
            super(message);
        }
        public CardResponseDTO processarPagamento(CardRequestDTO request) {
            // Converter DTO para entidade de domínio
            Transaction transaction = CardMapper.toTransactionEntity(request);
            
            // Processar pagamento (lógica existente)
            CardResponseDTO response = processInternal(request, transaction.getId());
            /**
             * 
             * The method getId() is undefined for the type Transaction
             * 
             * **/
            
            
            // Atualizar entidade com resposta
            CardMapper.updateEntityFromResponse(transaction, response);
            
            // Auditoria
            auditService.saveTransaction(transaction);
            /**
             * 
             * Cannot make a static reference to the non-static field auditService
             * 
             * **/
            
            return response;
        }
        
    }
}