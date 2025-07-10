package com.pagamento.boleto.infrastructure.adapters.gateway;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.pagamento.boleto.domain.exception.GatewayIntegrationException;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;

@Component
public class AsaasGatewayAdapter implements AsaasGatewayPort {

    @Override
    public String registrarBoleto(Boleto boleto) throws GatewayIntegrationException {
        return "mock-id-asaas";
    }

    @Override
    public void cancelarBoleto(String idExterno) throws GatewayIntegrationException {
        System.out.println("Cancelado no ASAAS: " + idExterno);
    }

    @Override
    public void confirmarPagamento(String idExterno, LocalDate dataPagamento) throws GatewayIntegrationException {
        System.out.println("Pagamento confirmado no ASAAS: " + idExterno + " em " + dataPagamento);
    }

    @Override
    public String enviarBoleto(String identificador, BigDecimal valor) {
        System.out.println("Boleto enviado para ASAAS: " + identificador + " valor: " + valor);
    }
}
