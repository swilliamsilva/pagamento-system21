// CardApplication.java
package com.pagamento.card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CardApplication {
    public static void main(String[] args) {
        SpringApplication.run(CardApplication.class, args);
        System.out.println("Card Service iniciado com sucesso!");
    }
}