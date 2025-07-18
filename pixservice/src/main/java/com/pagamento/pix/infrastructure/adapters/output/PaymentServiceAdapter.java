package com.pagamento.pix.infrastructure.adapters.output;

import com.pagamento.common.payment.PaymentOrchestratorPort;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.infrastructure.clients.PaymentServiceClient;
import com.pagamento.pix.infrastructure.integration.PaymentFailedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException; // Corrigida a importação

@Component
public class PaymentServiceAdapter implements PaymentOrchestratorPort {

    private final PaymentServiceClient paymentServiceClient;

    public PaymentServiceAdapter(PaymentServiceClient paymentServiceClient) {
        this.paymentServiceClient = paymentServiceClient;
    }

    @Override
    public TransactionResponse orchestrate(Pix pix) throws PaymentFailedException {
        try {
            return paymentServiceClient.processPayment(
                new PaymentRequest(
                    pix.getChaveOrigem().getValor(),
                    pix.getChaveDestino().getValor(),
                    pix.getValor()
                )
            );
        } catch (RestClientException e) { // Usando RestClientException
            throw new PaymentFailedException("Falha na comunicação com o serviço de pagamentos", e);
        }
    }
}