package com.pagamento.pix.infrastructure.clients;

import com.pagamento.pix.infrastructure.adapters.output.PaymentRequest;
import com.pagamento.pix.infrastructure.adapters.output.TransactionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentServiceClient {

    private final RestTemplate restTemplate;
    
    public PaymentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public TransactionResponse processPayment(PaymentRequest request) {
        // Implementação real da chamada HTTP
        return restTemplate.postForObject("/payments", request, TransactionResponse.class);
    }
}