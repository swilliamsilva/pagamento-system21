package com.pagamento.boleto.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EmbeddedKafka
class BoletoKafkaTest {
    @Autowired
    private EmbeddedKafkaBroker kafka;
}