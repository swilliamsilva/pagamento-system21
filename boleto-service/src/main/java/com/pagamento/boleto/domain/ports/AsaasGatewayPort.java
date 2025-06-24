package com.pagamento.boleto.domain.ports;

import java.time.LocalDate;

import com.pagamento.boleto.domain.exception.GatewayIntegrationException;
import com.pagamento.boleto.domain.model.Boleto;

public interface AsaasGatewayPort {
    String registrarBoleto(Boleto boleto) throws GatewayIntegrationException;
    void cancelarBoleto(String idExterno) throws GatewayIntegrationException;
    void confirmarPagamento(String idExterno, LocalDate dataPagamento) throws GatewayIntegrationException;
	void confirmarPagamento(Object idExterno, LocalDate dataPagamento);
}