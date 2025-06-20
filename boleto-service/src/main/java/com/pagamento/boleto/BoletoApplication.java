/* ========================================================
# Classe: BoletoApplication
# Módulo: boleto-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Classe principal do serviço de boletos.
# ======================================================== */

package com.pagamento.boleto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoletoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoletoApplication.class, args);
        System.out.println("Boleto Service iniciado com sucesso!");
    }
}
