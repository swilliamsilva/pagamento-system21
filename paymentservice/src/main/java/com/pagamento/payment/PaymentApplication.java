/* ========================================================
# Classe: PaymentApplication
# Módulo: payment-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Classe principal do serviço de orquestração de pagamentos.
# ======================================================== */

package com.pagamento.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
