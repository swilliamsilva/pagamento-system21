package com.pagamento.payment.service;

import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse processarPagamento(PaymentRequest request);
}

