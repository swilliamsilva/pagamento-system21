package com.pagamento.common.mapper;

import com.pagamento.common.model.Payment;
import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request) {
        validateRequest(request);
        
        Payment payment = new Payment();
        payment.setTransactionId(generateTransactionId());
        payment.setUserId(request.userId());
        payment.setPaymentType(request.tipoPagamento());
        payment.setAmount(request.valor());
        payment.setCreatedAt(Instant.now());
        payment.setStatus("CREATED");
        
        return payment;
    }

    // Método único toResponse (removido o duplicado)
    public PaymentResponse toResponse(Payment entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Payment entity cannot be null");
        }
        
        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(entity.getTransactionId());
        response.setStatus(entity.getStatus());
        response.setPaymentType(entity.getPaymentType());
        response.setAmount(entity.getAmount());
        
        return response;
    }

    public PaymentResponse toProcessingResponse(Payment entity) {
        PaymentResponse response = toResponse(entity);
        response.setStatus("PROCESSING");
        return response;
    }

    private String generateTransactionId() {
        return "TX-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private void validateRequest(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PaymentRequest cannot be null");
        }
        if (request.userId() == null || request.userId().isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.tipoPagamento() == null || request.tipoPagamento().isBlank()) {
            throw new IllegalArgumentException("Payment type is required");
        }
        validateAmount(request.valor());
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Payment amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    }
}