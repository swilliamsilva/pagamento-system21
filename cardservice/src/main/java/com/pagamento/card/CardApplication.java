package com.pagamento.card;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CardApplication {

    private static final Logger log = LoggerFactory.getLogger(CardApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CardApplication.class, args);
        log.info("Card Service iniciado com sucesso!");
    }
}