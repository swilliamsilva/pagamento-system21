package com.pagamento.common.payment;

import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.infrastructure.adapters.output.TransactionResponse;
import com.pagamento.pix.infrastructure.integration.PaymentFailedException;

public interface PaymentOrchestratorPort {
    TransactionResponse orchestrate(Pix pix) throws PaymentFailedException;
}