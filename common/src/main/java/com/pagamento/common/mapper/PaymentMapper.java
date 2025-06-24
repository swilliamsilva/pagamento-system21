package com.pagamento.common.mapper;

import com.pagamento.common.model.Payment;
import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(String userId, String paymentType, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setTransactionId(generateTransactionId());
        payment.setUserId(userId);
        payment.setPaymentType(paymentType);
        payment.setAmount(validateAmount(amount));
        payment.setCreatedAt(Instant.now());
        return payment;
    }

    public Payment toSimpleOutput(Payment entity) {
        if (entity == null) return null;
        
        Payment output = new Payment();
        output.setTransactionId(entity.getTransactionId());
        output.setPaymentType(entity.getPaymentType());
        output.setAmount(entity.getAmount());
        return output;
    }

    public Payment toApiResponse(Payment entity, String status) {
        if (entity == null) return null;
        
        Payment response = new Payment();
        response.setTransactionId(entity.getTransactionId());
        response.setStatus(status);
        response.setAmount(entity.getAmount());
        response.setPaymentType(entity.getPaymentType());
        return response;
    }

    public Payment toApiResponse(Payment entity) {
        return toApiResponse(entity, "PROCESSANDO");
    }

    private String generateTransactionId() {
        return "TX-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Valor do pagamento não pode ser nulo");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser positivo");
        }
        return amount;
    }

	public static Payment toEntity(@Valid PaymentRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public static PaymentResponse toResponse(Payment entity) {
		// TODO Auto-generated method stub
		return null;
	}
}