package com.pagamento.boletoservice;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;

@SpringBootTest
@ActiveProfiles("jpa")
public class JpaBoletoRepositoryTest extends AbstractBoletoRepositoryTest {
    
    @Autowired
    private BoletoRepositoryPort jpaRepository;
    
    @BeforeEach
    public void init() {
        this.repository = jpaRepository;
        super.setUpBase();
    }
    
    @Test
    @Transactional
    @Override
    public void deveSalvarEBuscarBoleto() {
        // Implementação específica para JPA
        testSave();
        testFindById();
    }
}