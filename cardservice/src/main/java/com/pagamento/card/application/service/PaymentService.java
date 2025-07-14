package com.pagamento.card.application.service;

import com.pagamento.card.application.dto.*;
import com.pagamento.card.application.mapper.CardMapper;
import com.pagamento.card.domain.enums.PaymentStatus;
import com.pagamento.card.domain.model.Transaction;
import com.pagamento.card.domain.strategy.BandeiraStrategy;
import com.pagamento.core.common.resilience.ResilienceManager;
import com.pagamento.core.common.metrics.PaymentMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    
    private final ApplicationContext context;
    private final BandeiraStrategy fallbackStrategy;
    private final PaymentMetrics paymentMetrics;
    private final AuditService auditService;
    
    @Value("${payment.environment:PRODUCTION}")
    private String environment;

    @Autowired
    public PaymentService(ApplicationContext context,
                         BandeiraStrategy fallbackStrategy,
                         PaymentMetrics paymentMetrics,
                         AuditService auditService) {
        this.context = context;
        this.fallbackStrategy = fallbackStrategy;
        this.paymentMetrics = paymentMetrics;
        this.auditService = auditService;
    }

    public CardResponseDTO processarPagamento(CardRequestDTO request) {
        final String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        
        try {
            log.info("Processando pagamento: {}", request.getBandeira());
            // Fixed metrics method call - removed null parameter
            paymentMetrics.incrementStatus(request.getBandeira());
            
            CardResponseDTO response = ResilienceManager.executeWithCircuitBreaker(
                "payment-circuit-" + request.getBandeira(),
                () -> processInternal(request, transactionId),
                () -> fallbackResponse(request, transactionId)
            );
            
            // Convert to entity and audit AFTER processing
            Transaction transaction = CardMapper.toTransactionEntity(request);
            CardMapper.updateEntityFromResponse(transaction, response);
            auditService.saveTransaction(transaction);
            
            return response;
        } finally {
            log.info("Finalizado processamento para TXN: {}", transactionId);
        }
    }
    
    private CardResponseDTO processInternal(CardRequestDTO request, String transactionId) {
        String bandeiraKey = resolveStrategyKey(request);
        
        if (context.containsBean(bandeiraKey)) {
            BandeiraStrategy strategy = context.getBean(bandeiraKey, BandeiraStrategy.class);
            CardResponseDTO response = strategy.processarPagamento(request);
            response.setTransactionId(transactionId);
            
            // Async auditing
            CompletableFuture.runAsync(() -> auditService.saveTransaction(response));
            
            return response;
        }
        
        return fallbackStrategy.processarPagamento(request);
    }
    
    private CardResponseDTO fallbackResponse(CardRequestDTO request, String transactionId) {
        CardResponseDTO response = new CardResponseDTO(
            transactionId,
            request.getBandeira(),
            PaymentStatus.PROCESSING_ERROR,
            request.getValor(),
            null,
            "Sistema temporariamente indisponível"
        );
        
        paymentMetrics.incrementError(request.getBandeira(), "CircuitBreakerOpen");
        auditService.saveTransaction(response);
        
        return response;
    }
    
    private String resolveStrategyKey(CardRequestDTO request) {
        String bandeira = request.getBandeira().toUpperCase();
        
        if ("SANDBOX".equalsIgnoreCase(environment)) {
            String sandboxKey = bandeira + "_SANDBOX";
            if (context.containsBean(sandboxKey)) {
                return sandboxKey;
            }
        }
        
        return bandeira;
    }
}