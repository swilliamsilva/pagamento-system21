/* ========================================================
# Classe: AuthApplication
# Módulo: auth-service
# Projeto: pagamento-system21
# Autor: William Silva
# Contato: williamsilva.codigo@gmail.com
# Website: simuleagora.com
# Descrição: Inicializa o serviço de autenticação JWT.
# ======================================================== */

package com.pagamento.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
