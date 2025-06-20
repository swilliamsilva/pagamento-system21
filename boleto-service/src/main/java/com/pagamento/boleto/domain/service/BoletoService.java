/* ========================================================
# Classe: BoletoService
# Módulo: boleto-service (Domínio)
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Serviço de domínio responsável pela orquestração da geração de boletos.
# ======================================================== */

package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;
import com.pagamento.boleto.domain.ports.NotificacaoPort;


/**
 * The import com.pagamento.boleto.domain.model cannot be resolved
 * 
 * The import com.pagamento.boleto.domain.ports.AsaasGatewayPort cannot be resolved
 * 
 * The import com.pagamento.boleto.domain.ports.NotificacaoPort cannot be resolved
 * 
 * **/

public class BoletoService {

    private final BoletoRepositoryPort repository;
    private final AsaasGatewayPort asaasGateway;
    /**
     * 
     * AsaasGatewayPort cannot be resolved to a type
     * **/
    
    private final NotificacaoPort notificacaoPort;
    
    /**
     * 
     * NotificacaoPort cannot be resolved to a type
     * 
     * **/

    public BoletoService(BoletoRepositoryPort repository, AsaasGatewayPort asaasGateway, NotificacaoPort notificacaoPort) {
        /**
         * Multiple markers at this line
	- NotificacaoPort cannot be resolved to a type
	- AsaasGatewayPort cannot be resolved to a type
         * 
         * 
         * **/
    	
    	
    	this.repository = repository;
        this.asaasGateway = asaasGateway;
        /**
         * 
         * AsaasGatewayPort cannot be resolved to a type
         * 
         * 
         * **/
        
        this.notificacaoPort = notificacaoPort;
        /**
         * 
         * NotificacaoPort cannot be resolved to a type
         * 
         * 
         * **/
        
        
    }

    public void gerar(Boleto boleto) {
        // Validações e cálculos podem ser delegados
        BoletoCalculos.aplicarTaxas(boleto);
        /**
         * BoletoCalculos cannot be resolved
         * 
         * 
         * **/
        
        
        BoletoValidation.validar(boleto);
        /**
         * 
         * 
         * BoletoValidation cannot be resolved
         * 
         * **/
        

        // Persistência
        repository.salvar(boleto);

        // Integração externa
        asaasGateway.registrar(boleto);
        
        /**
         * 
         * 
         * 
         * 
         * **/
        

        // Notificação
        notificacaoPort.enviarNotificacao("Boleto gerado com sucesso para: " + boleto.getDescricao());
   /**
    * 
    * NotificacaoPort cannot be resolved to a type
    * 
    * 
    * **/
    
    }
}
