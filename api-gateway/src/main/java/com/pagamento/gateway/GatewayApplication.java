/* ========================================================
# Classe: GatewayApplication
# Módulo: api-gateway
# Projeto: pagamento-system21
# Autor: William Silva
# Contato: williamsilva.codigo@gmail.com
# Website: simuleagora.com
# Descrição: Classe principal de inicialização do API Gateway.
# ======================================================== */

package com.pagamento.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * The import org.springframework cannot be resolved
 * 
 * The import org.springframework cannot be resolved
 * 
 * **/




/**
 * Esta é a classe principal que inicializa o módulo API Gateway
 * do projeto pagamento-system21.
 *
 * O Gateway atua como ponto de entrada para todas as requisições,
 * sendo responsável pelo roteamento, segurança e controle de tráfego.
 *
 * O projeto segue a arquitetura hexagonal, mas o gateway em si é
 * estruturado como um ponto de orquestração/rest proxy.
 */
@SpringBootApplication

/**
 * 
 * 
 * SpringBootApplication cannot be resolved to a type
 * 
 * 
 * **/


public class GatewayApplication {

    /**
     * Método principal que executa a aplicação Spring Boot.
     * Ao iniciar, carrega o contexto da aplicação e disponibiliza
     * o endpoint do Swagger e demais controllers.
     *
     * Acesse a documentação em:
     * http://localhost:8080/swagger-ui.html
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        
        /**
         * 
         * 
         * 
         * SpringApplication cannot be resolved
         * 
         * 
         * **/
        
    }
}
