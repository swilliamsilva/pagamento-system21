package com.pagamento.payment.adapter.output.mongodb;

import com.pagamento.payment.model.Payment;
import com.pagamento.payment.port.output.PaymentRepositoryPort;
import com.pagamento.payment.repository.mongo.PaymentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMongoRepositoryAdapter implements PaymentRepositoryPort {
    /**
     * 
     * The type PaymentMongoRepositoryAdapter must implement the inherited abstract method PaymentRepositoryPort.salvar(Payment)
     * **/
    private final PaymentMongoRepository repository = null;
    
    @Override
    public Payment salvar(Payment payment) {
        return repository.save(payment);
    }
}