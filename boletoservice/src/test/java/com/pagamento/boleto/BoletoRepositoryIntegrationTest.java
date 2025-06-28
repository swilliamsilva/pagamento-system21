package com.pagamento.boleto;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.flyway.AutoConfigureFlyway;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

@DataJpaTest
@AutoConfigureFlyway
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BoletoRepositoryIntegrationTest {

    @Autowired
    private SpringDataBoletoRepository repository;

    @Test
    void shouldApplyMigrationsAndStartApplicationContext() {
        // Verifica se o Flyway aplicou as migrações
        assertThat(repository.count()).isZero();
    }
}