package com.pagamento.boleto.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;

@Component
public class AsaasClient implements AsaasGatewayPort {

    @Override
    public String registrarBoleto(Boleto boleto) {
        // Simulação de integração com API Asaas
        return "asaas_pay_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public void cancelarBoleto(String idExterno) {
        // Simulação de cancelamento na API Asaas
        if (!idExterno.startsWith("asaas_pay_")) {
            throw new IllegalArgumentException("ID externo inválido");
        }
    }

    @Override
    public BoletoStatus consultarBoleto(String idExterno) {
        // Simulação de consulta na API Asaas
        if (!idExterno.startsWith("asaas_pay_")) {
            throw new IllegalArgumentException("ID externo inválido");
        }
        return new BoletoStatus("PENDING", LocalDate.now().plusDays(30).toString());
        /**
         * Cannot instantiate the type BoletoStatus
         * 
         * **/
    }

    @Override
    public PagamentoStatus confirmarPagamento(String idExterno) {
        // Simulação de confirmação de pagamento na API Asaas
        if (!idExterno.startsWith("asaas_pay_")) {
            throw new IllegalArgumentException("ID externo inválido");
        }
        return new PagamentoStatus("CONFIRMED", LocalDateTime.now().toString());
    }

    @Override
    public String criarCobranca(BoletoRequestDTO request, String customerId) {
        // Implementação real da criação de cobrança
        return "asaas_charge_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public Object consultarCobranca(String cobrancaId) {
        // Implementação real da consulta de cobrança
        if (!cobrancaId.startsWith("asaas_charge_")) {
            throw new IllegalArgumentException("ID de cobrança inválido");
        }
        
        return Map.of(
            "id", cobrancaId,
            "status", "PENDING",
            "value", 1000.00,
            "dueDate", LocalDate.now().plusDays(30).toString()
        );
    }

	@Override
	public String enviarBoleto(String id, BigDecimal valor) {
		// TODO Auto-generated method stub
		
		/**
		 * Tem que implementar
		 * 
		 * **/
		return null;
	}
}