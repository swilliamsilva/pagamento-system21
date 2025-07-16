package com.pagamento.pix.infrastructure.adapters.output;

import com.pagamento.common.payment.PaymentOrchestratorPort;
import com.pagamento.common.payment.TransactionResponse;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.infrastructure.clients.PaymentServiceClient;
import org.springframework.stereotype.Component;

@Component
public class PaymentServiceAdapter implements PaymentOrchestratorPort {

    private final PaymentServiceClient paymentServiceClient;

    public PaymentServiceAdapter(PaymentServiceClient paymentServiceClient) {
        this.paymentServiceClient = paymentServiceClient;
    }

    @Override
    public TransactionResponse orchestrate(Pix pix) {
        return paymentServiceClient.processPayment(
            new PaymentRequest(
                pix.getChaveOrigem(),
                pix.getChaveDestino(),
                pix.getValor()
            )
        );
    }
}