package com.pagamento.boleto.domain.ports;

import com.pagamento.boleto.domain.exception.GatewayIntegrationException;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;

public interface AsaasGatewayPort {
    
    /**
     * Registra um novo boleto no gateway de pagamento Asaas
     * 
     * @param boleto Boleto a ser registrado
     * @return ID externo gerado pelo gateway
     * @throws GatewayIntegrationException em caso de falha na integração
     */
    String registrarBoleto(Boleto boleto) throws GatewayIntegrationException;
    
    /**
     * Cancela um boleto no gateway de pagamento
     * 
     * @param idExterno ID do boleto no sistema externo
     * @throws GatewayIntegrationException em caso de falha na integração
     */
    void cancelarBoleto(String idExterno) throws GatewayIntegrationException;
    
    /**
     * Consulta o status atual de um boleto no gateway
     * 
     * @param idExterno ID do boleto no sistema externo
     * @return Status atual do boleto
     * @throws GatewayIntegrationException em caso de falha na integração
     */
    BoletoStatus consultarStatusBoleto(String idExterno) throws GatewayIntegrationException;
    
    /**
     * Confirma o pagamento de um boleto no gateway
     * 
     * @param idExterno ID do boleto no sistema externo
     * @return Status atualizado após confirmação
     * @throws GatewayIntegrationException em caso de falha na integração
     */
    BoletoStatus confirmarPagamento(String idExterno) throws GatewayIntegrationException;
}