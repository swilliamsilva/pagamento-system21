package com.pagamento.boleto.domain.ports;

import com.pagamento.boleto.domain.exception.NotificationException;
import com.pagamento.boleto.domain.model.Boleto;

public interface NotificacaoPort {
    
    /**
     * Notifica a emissão de um novo boleto
     * 
     * @param boleto Boleto emitido
     * @throws NotificationException em caso de falha no envio
     */
    void notificarEmissao(Boleto boleto) throws NotificationException;
    
    /**
     * Notifica a reemissão de um boleto
     * 
     * @param original Boleto original
     * @param reemissao Boleto reemitido
     * @throws NotificationException em caso de falha no envio
     */
    void notificarReemissao(Boleto original, Boleto reemissao) throws NotificationException;
    
    /**
     * Notifica o cancelamento de um boleto
     * 
     * @param boleto Boleto cancelado
     * @throws NotificationException em caso de falha no envio
     */
    void notificarCancelamento(Boleto boleto) throws NotificationException;
    
    /**
     * Notifica o pagamento de um boleto
     * 
     * @param boleto Boleto pago
     * @throws NotificationException em caso de falha no envio
     */
    void notificarPagamento(Boleto boleto) throws NotificationException;
}