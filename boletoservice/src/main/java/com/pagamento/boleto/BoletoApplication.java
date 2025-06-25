package com.pagamento.boleto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.pagamento.boleto.infrastructure.config.BoletoConfig;

@SpringBootApplication
@Import(BoletoConfig.class)
public class BoletoApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoletoApplication.class, args);
    }
}