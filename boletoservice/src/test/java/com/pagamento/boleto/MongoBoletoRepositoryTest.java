package com.pagamento.boleto;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;

@SpringBootTest
@ActiveProfiles("mongo")
class MongoBoletoRepositoryTest extends AbstractBoletoRepositoryTest {

    @Autowired
    private BoletoRepositoryPort mongoRepository;

    @BeforeEach
    public void init() {
        this.repository = mongoRepository;
        super.setUpBase();
    }

    @Test
    @Override
    public void deveSalvarEBuscarBoleto() {
        testSave();
        testFindById();
    }
}