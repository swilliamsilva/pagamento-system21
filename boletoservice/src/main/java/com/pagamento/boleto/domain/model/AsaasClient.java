package com.pagamento.boleto.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;

@Component
public class AsaasClient implements AsaasGatewayPort {

    // Constantes para evitar duplicação
    private static final String ASAAS_PAY_PREFIX = "asaas_pay_";
    private static final String ASAAS_CHARGE_PREFIX = "asaas_charge_";
    private static final String INVALID_ID_MESSAGE = "ID externo inválido: ";

    @Override
    public String registrarBoleto(Boleto boleto) {
        validateBoleto(boleto);
        return generateAsaasId(ASAAS_PAY_PREFIX);
    }

    @Override
    public void cancelarBoleto(String idExterno) {
        validateAsaasId(idExterno, ASAAS_PAY_PREFIX);
        // Lógica real de cancelamento seria implementada aqui
    }

    @Override
    public BoletoStatus consultarBoleto(String idExterno) {
        validateAsaasId(idExterno, ASAAS_PAY_PREFIX);
        
        // Simulação de diferentes status baseados no ID
        if (idExterno.contains("_expired")) {
            return BoletoStatus.VENCIDO;
        } else if (idExterno.contains("_paid")) {
            return BoletoStatus.PAGO;
        }
        return BoletoStatus.EMITIDO;
    }

    @Override
    public PagamentoStatus confirmarPagamento(String idExterno) {
        validateAsaasId(idExterno, ASAAS_PAY_PREFIX);
        
        // Simulação de status de pagamento
        String status = idExterno.contains("_fail") ? "FAILED" : "CONFIRMED";
        return new PagamentoStatus(status, LocalDateTime.now());
    }

    @Override
    public String criarCobranca(BoletoRequestDTO request, String customerId) {
        validateBoletoRequest(request);
        validateCustomerId(customerId);
        
        return generateAsaasId(ASAAS_CHARGE_PREFIX);
    }

    @Override
    public Map<String, Object> consultarCobranca(String cobrancaId) {
        validateAsaasId(cobrancaId, ASAAS_CHARGE_PREFIX);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", cobrancaId);
        response.put("status", cobrancaId.contains("_paid") ? "PAID" : "PENDING");
        response.put("value", 1000.00);
        response.put("dueDate", LocalDate.now().plusDays(30).toString());
        return response;
    }

    @Override
    public String enviarBoleto(String id, BigDecimal valor) {
        validateId(id);
        validateValor(valor);
        
        // Simulação de envio - retorna ID de transação
        return generateAsaasId(ASAAS_PAY_PREFIX) + "_sent";
    }

    // =============== MÉTODOS AUXILIARES ===============
    
    private String generateAsaasId(String prefix) {
        return prefix + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private void validateAsaasId(String id, String expectedPrefix) {
        if (id == null || !id.startsWith(expectedPrefix)) {
            throw new IllegalArgumentException(INVALID_ID_MESSAGE + id);
        }
    }
    
    private void validateBoleto(Boleto boleto) {
        if (boleto == null) {
            throw new IllegalArgumentException("Boleto não pode ser nulo");
        }
        if (boleto.getValor() == null || boleto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do boleto inválido");
        }
    }
    
    private void validateBoletoRequest(BoletoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("BoletoRequestDTO não pode ser nulo");
        }
        // Adicione outras validações necessárias
    }
    
    private void validateCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID não pode ser vazio");
        }
    }
    
    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID não pode ser vazio");
        }
    }
    
    private void validateValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
    }
}