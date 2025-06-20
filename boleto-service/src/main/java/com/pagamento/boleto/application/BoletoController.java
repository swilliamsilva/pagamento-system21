/* ========================================================
# Classe: BoletoController
# Módulo: boleto-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Camada de entrada (application) para o serviço de boletos.
# ======================================================== */

package com.pagamento.boleto.application;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.application.dto.BoletoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * 
 * The import org.springframework cannot be resolved
 * The import org.springframework cannot be resolved
 * 
 * 
 * 
 * **/


@RestController
/**
 * 
 * RestController cannot be resolved to a type
 * 
 * 
 * **/


@RequestMapping("/api/boleto")
/**
 * 
 * 
 * RequestMapping cannot be resolved to a type
 * 
 * 
 * **/


public class BoletoController {

    @PostMapping
    /**
     * 
     * PostMapping cannot be resolved to a type
     * 
     * 
     * **/
    
    
    public ResponseEntity<BoletoResponseDTO> gerarBoleto(@RequestBody BoletoRequestDTO request) {
        /**
         * 
         * Multiple markers at this line
	- RequestBody cannot be resolved to a type
	- ResponseEntity cannot be resolved to a type
         * 
         * **/
    	
    	
    	// TODO: Chamar serviço de domínio (via port)
        BoletoResponseDTO response = new BoletoResponseDTO("123456789", request.valor(), "boleto gerado com sucesso");
        return ResponseEntity.ok(response);
        /**
         * 
         * 
         * 
         * ResponseEntity cannot be resolved
         * 
         * **/
        
        
    }
}
