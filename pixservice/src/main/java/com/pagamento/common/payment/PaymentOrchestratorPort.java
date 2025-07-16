// common/src/main/java/com/pagamento/common/payment
package com.pagamento.common.payment;

import com.pagamento.pix.domain.model.Pix;
import com.pagamento.common.payment.TransactionResponse;

public interface PaymentOrchestratorPort {
    TransactionResponse orchestrate(Pix pix);
}