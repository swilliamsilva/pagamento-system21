/* ========================================================
# Classe: PixApplication
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Classe principal do serviço Pix.
# ======================================================== */

package com.pagamento.pix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PixApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixApplication.class, args);
        System.out.println("Pix Service iniciado com sucesso!");
    }
}
