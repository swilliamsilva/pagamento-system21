package com.pagamento.payment;

import com.pagamento.payment.model.Payment;
import com.pagamento.payment.repository.mongo.PaymentMongoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentMongoRepository repository;

    @Test
    void devePersistirPagamentoNoMongoDB() {
        Payment pagamento = new Payment();
        pagamento.setTipo("PIX");
        pagamento.setValor(150.0);
        
        Payment salvo = repository.save(pagamento);
        
        assertThat(salvo.getId()).isNotNull();
        assertThat(repository.findById(salvo.getId())).isPresent();
    }
}