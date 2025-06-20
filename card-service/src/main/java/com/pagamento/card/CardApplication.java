/* ========================================================
# Classe: CardApplication
# Módulo: card-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Classe principal do serviço de cartão.
# ======================================================== */

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
