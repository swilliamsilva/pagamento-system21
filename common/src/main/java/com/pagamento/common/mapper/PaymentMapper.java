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

    public static Payment toEntity(PaymentRequest request) {
        validateRequest(request);
        
        return Payment.builder()
            .transactionId(generateTransactionId())
            .userId(request.userId())
            .paymentType(request.tipoPagamento())
            .amount(request.valor())
            .createdAt(Instant.now())
            .status("CREATED")
            .build();
    }

    public static PaymentResponse toResponse(Payment entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Payment entity cannot be null");
        }
        
        return PaymentResponse.builder()
            .transactionId(entity.getTransactionId())
            .status(entity.getStatus())
            .paymentType(entity.getPaymentType())
            .amount(entity.getAmount())
            .build();
    }

    public static PaymentResponse toProcessingResponse(Payment entity) {
        PaymentResponse response = toResponse(entity);
        response.setStatus("PROCESSING");
        return response;
    }

    private static String generateTransactionId() {
        return "TX-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private static void validateRequest(PaymentRequest request) {
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

    private static void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Payment amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    }
}

