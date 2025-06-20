/* ========================================================
# Classe: SwaggerConfig
# Módulo: pagamento-gateway
# Projeto: pagamento_system21
# Autor: William Silva
# Contato: williamsilva.codigo@gmail.com
# Website: simuleagora.com
# Descrição: Configuração da documentação da API com Swagger/OpenAPI 3
# ======================================================== */

package com.pagamento.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The import io cannot be resolved
 * 
 * The import org.springframework cannot be resolved
 * 
 * 
 * 
 * 
 * **/



@Configuration

/**
 * Configuration cannot be resolved to a type
 * 
 * 
 * 
 * **/

public class SwaggerConfig {

    /**
     * Cria o bean OpenAPI para customizar a documentação da API
     * usando a especificação OpenAPI 3 via SpringDoc.
     *
     * @return objeto OpenAPI configurado
     */
    @Bean
    /**
     * 
     * 
     * Bean cannot be resolved to a type
     * 
     * 
     * **/
    
    public OpenAPI customOpenAPI() {
    	
    	/**
    	 * OpenAPI cannot be resolved to a type
    	 * 
    	 * 
    	 * **/
        return new OpenAPI()
        		/**
        		 * 
        		 * OpenAPI cannot be resolved to a type
        		 * 
        		 * **/
        		
            .info(new Info()
            		/**
            		 * 
            		 * 
            		 * Info cannot be resolved to a type
            		 * 
            		 * 
            		 * **/
            		
                .title("API Gateway - Sistema de Pagamentos")
                .version("1.0.0")
                .description("Documentação da API Gateway do sistema de pagamentos, parte do projeto pagamento_system21.")
                .contact(new Contact()
                		/**
                		 * 
                		 * 
                		 * Contact cannot be resolved to a type
                		 * 
                		 * 
                		 * **/
                		
                    .name("William Silva")
                    .email("williamsilva.codigo@gmail.com")
                    .url("https://simuleagora.com"))
                .license(new License()
                		
                		/**
                		 * 
                		 * License cannot be resolved to a type
                		 * 
                		 * 
                		 * **/
                		
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
