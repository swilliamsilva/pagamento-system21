package com.pagamento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.pagamento.payment.PaymentApplication;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentApplicationTests {

    @Test
    void contextLoads(ApplicationContext context) {
        // Verifica se o contexto do Spring é carregado corretamente
        assertThat(context).isNotNull();
    }

    @Test
    void mainMethodStartsApplication() {
        // Testa o método main para garantir que a aplicação inicia sem erros
        PaymentApplication.main(new String[]{});
    }
}