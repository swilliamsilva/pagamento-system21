package com.pagamento.boleto.infrastructure.adapters;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.model.AsaasClient;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.model.PagamentoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AsaasClientTest {

    private AsaasClient asaasClient;
    private Boleto boleto;
    private BoletoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        asaasClient = new AsaasClient();
        
        boleto = new Boleto();
        boleto.setPagador("Cliente A");
        boleto.setBeneficiario("Beneficiário B");
        boleto.setValor(new BigDecimal("1000.00"));
        boleto.setDataVencimento(LocalDate.now().plusDays(30));
        
        requestDTO = new BoletoRequestDTO(
            "Cliente A",
            "Beneficiário B",
            1000.00,
            LocalDate.now().plusDays(30),
            "DOC-123",
            "Instruções",
            "Pagável em qualquer banco"
        );
    }

    @Test
    void registrarBoleto_DeveRetornarIdValido() {
        String idExterno = asaasClient.registrarBoleto(boleto);
        assertNotNull(idExterno);
        assertTrue(idExterno.startsWith("asaas_pay_"));
    }

    @Test
    void cancelarBoleto_ComIdValido_NaoDeveLancarExcecao() {
        String idExterno = "asaas_pay_123456";
        assertDoesNotThrow(() -> asaasClient.cancelarBoleto(idExterno));
    }

    @Test
    void cancelarBoleto_ComIdInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, 
            () -> asaasClient.cancelarBoleto("id_invalido"));
    }

    @Test
    void consultarBoleto_DeveRetornarStatusValido() {
        BoletoStatus status = asaasClient.consultarBoleto("asaas_pay_123456");
        assertNotNull(status);
        assertEquals("PENDING", status.getStatus());
        assertNotNull(status.getDueDate());
    }

    @Test
    void confirmarPagamento_DeveRetornarStatusConfirmado() {
        PagamentoStatus status = asaasClient.confirmarPagamento("asaas_pay_123456");
        assertNotNull(status);
        assertEquals("CONFIRMED", status.getStatus());
        assertNotNull(status.getConfirmedDate());
    }

    @Test
    void criarCobranca_DeveRetornarIdValido() {
        String customerId = "cus_123456";
        String cobrancaId = asaasClient.criarCobranca(requestDTO, customerId);
        assertNotNull(cobrancaId);
        assertTrue(cobrancaId.startsWith("asaas_charge_"));
    }

    @Test
    void consultarCobranca_DeveRetornarDadosValidos() {
        String cobrancaId = "asaas_charge_123456";
        Object response = asaasClient.consultarCobranca(cobrancaId);
        
        assertNotNull(response);
        assertTrue(response instanceof Map);
        
        Map<?, ?> cobranca = (Map<?, ?>) response;
        assertEquals(cobrancaId, cobranca.get("id"));
        assertEquals("PENDING", cobranca.get("status"));
        assertEquals(1000.00, cobranca.get("value"));
        assertNotNull(cobranca.get("dueDate"));
    }

    @Test
    void consultarCobranca_ComIdInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, 
            () -> asaasClient.consultarCobranca("id_invalido"));
    }
}