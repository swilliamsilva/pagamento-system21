package tests.paymentservice;

import com.pagamento.payment.PaymentApplication;
import com.pagamento.payment.model.Payment;
import com.pagamento.payment.repository.mongo.PaymentMongoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PaymentApplication.class)
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentMongoRepository repository;

    @Test
    void deveSalvarEPersistirPagamento() {
        Payment pagamento = new Payment("123", 100.0, "pix");
        Payment salvo = repository.save(pagamento);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getValor()).isEqualTo(100.0);
    }
}
