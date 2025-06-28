package com.pagamento.payment.dto.mapper;

import com.pagamento.payment.enums.PaymentType;
import com.pagamento.payment.model.Payment;
import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    
    public Payment toEntity(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setTipo(request.tipo());
        payment.setValor(request.valor());
        return payment;
    }

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
            "Pagamento processado: " + payment.getTipo(),
            payment.getId(),
            payment.getValor(),
            payment.getData()
        );
    }

	public Payment toEntity(PaymentRequest request, PaymentType paymentType) {
		// TODO Auto-generated method stub
		return null;
	}
}