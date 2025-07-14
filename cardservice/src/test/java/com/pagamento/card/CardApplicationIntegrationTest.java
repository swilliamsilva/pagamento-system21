package com.pagamento.card;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class CardApplicationIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void deveCarregarContexto() {
        // Verifica se o contexto do Spring é carregado corretamente
        assertThat(context).isNotNull();
    }

    @Test
    void deveIniciarAplicacaoQuandoMetodoMainForExecutado() {
        // Executa o método main
        CardApplication.main(new String[] {});
        
        // Verifica se o bean da aplicação principal foi carregado no contexto
        assertThat(context.containsBean("cardApplication")).isTrue();
    }
}