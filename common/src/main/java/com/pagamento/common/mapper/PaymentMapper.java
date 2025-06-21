package com.pagamento.common.mapper;
/***
 * Orquestrador de pagamento
 * 
 * 
 */


import com.pagamento.common.dto.PaymentDTO;
import com.pagamento.common.dto.PaymentRequest;
import com.pagamento.common.dto.PaymentResponse;
import com.pagamento.common.model.Payment;

import java.util.Date;
import java.util.UUID;

/**
 * Mapper para conversão entre modelos de pagamento.
 */
public class PaymentMapper {

    public static Payment toEntity(PaymentRequest request) {
        if (request == null) return null;

        Payment payment = new Payment();
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setUserId(request.userId());
        payment.setPaymentType(request.tipoPagamento());
        payment.setAmount(request.valor());
        payment.setCreatedAt(new Date());
        return payment;
    }

    public static PaymentDTO toDto(Payment entity) {
        if (entity == null) return null;

        return new PaymentDTO(
            entity.getTransactionId(),
            entity.getPaymentType(),
            entity.getAmount()
        );
    }

    public static PaymentResponse toResponse(Payment entity) {
        if (entity == null) return null;

        return new PaymentResponse(
            entity.getTransactionId(),
            "APROVADO", // ou pendente, etc. — pode vir do status real depois
            entity.getAmount(),
            entity.getPaymentType()
        );
    }
}
