package com.pagamento.boleto;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.infrastructure.persistence.SpringDataBoletoRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureFlyway
@ActiveProfiles("test")
class BoletoRepositoryTest {
    
    @Autowired
    private SpringDataBoletoRepository repository;
    
    @Test
    void shouldSaveBoleto() {
        Boleto boleto = new Boleto();
        boleto.setValor(new BigDecimal("100.00"));
        // ... outros campos
        
        Boleto saved = repository.save(boleto);
        assertNotNull(saved.getId());
    }
}