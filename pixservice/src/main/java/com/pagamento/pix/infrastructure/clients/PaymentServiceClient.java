package com.pagamento.pix.infrastructure.clients;

import com.pagamento.common.payment.PaymentRequest;
import com.pagamento.common.payment.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    name = "payment-service",
    url = "${payment.service.url}",
    configuration = PaymentServiceConfig.class
)
public interface PaymentServiceClient {
    
    @PostMapping("/payments/orchestrate")
    TransactionResponse processPayment(PaymentRequest request);
}