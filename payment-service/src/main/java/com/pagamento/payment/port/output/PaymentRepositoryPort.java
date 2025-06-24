package com.pagamento.payment.port.output;

import com.pagamento.payment.model.Payment;

public interface PaymentRepositoryPort {
    Payment salvar(Payment payment);
}