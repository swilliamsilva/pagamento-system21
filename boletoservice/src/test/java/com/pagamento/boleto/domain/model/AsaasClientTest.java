package com.pagamento.boleto.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class AsaasClientTest {

    private AsaasClient asaasClient;
    private Boleto boletoValido;

    @BeforeEach
    void setUp() {
        asaasClient = new AsaasClient();
        boletoValido = new Boleto();
        boletoValido.setValor(new BigDecimal("100.00"));
    }

    @Test
    void registrarBoleto_ComBoletoValido_RetornaIdValido() {
        String id = asaasClient.registrarBoleto(boletoValido);
        assertNotNull(id);
        assertTrue(id.startsWith(AsaasClient.ASAAS_PAY_PREFIX));
    }

    @Test
    void registrarBoleto_ComBoletoNulo_LancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> 
            asaasClient.registrarBoleto(null)
        );
    }

    @Test
    void cancelarBoleto_ComIdValido_ExecutaSemErros() {
        String idValido = AsaasClient.ASAAS_PAY_PREFIX + "12345678";
        assertDoesNotThrow(() -> asaasClient.cancelarBoleto(idValido));
    }

    @Test
    void consultarBoleto_ComIdValido_RetornaStatus() {
        String idValido = AsaasClient.ASAAS_PAY_PREFIX + "test123";
        BoletoStatus status = asaasClient.consultarBoleto(idValido);
        assertNotNull(status);
    }

    @Test
    void confirmarPagamento_ComIdValido_RetornaStatus() {
        String idValido = AsaasClient.ASAAS_PAY_PREFIX + "success";
        PagamentoStatus status = asaasClient.confirmarPagamento(idValido);
        assertEquals("CONFIRMED", status.getStatus());
        assertNotNull(status.getDataConfirmacao());
    }

    @Test
    void criarCobranca_ComDadosValidos_RetornaId() {
        BoletoRequestDTO request = new BoletoRequestDTO();
        String customerId = "cust_12345";
        String id = asaasClient.criarCobranca(request, customerId);
        assertTrue(id.startsWith(AsaasClient.ASAAS_CHARGE_PREFIX));
    }

    @Test
    void consultarCobranca_ComIdValido_RetornaDados() {
        String idValido = AsaasClient.ASAAS_CHARGE_PREFIX + "charge123";
        Map<String, Object> response = asaasClient.consultarCobranca(idValido);
        
        assertNotNull(response);
        assertEquals(idValido, response.get("id"));
        assertNotNull(response.get("dueDate"));
    }

    @Test
    void enviarBoleto_ComDadosValidos_RetornaId() {
        String id = "boleto_123";
        BigDecimal valor = new BigDecimal("150.00");
        String result = asaasClient.enviarBoleto(id, valor);
        
        assertNotNull(result);
        assertTrue(result.startsWith(AsaasClient.ASAAS_PAY_PREFIX));
        assertTrue(result.endsWith("_sent"));
    }

    @Test
    void enviarBoleto_ComValorInvalido_LancaExcecao() {
        String id = "boleto_123";
        BigDecimal valorInvalido = BigDecimal.ZERO;
        
        assertThrows(IllegalArgumentException.class, () -> 
            asaasClient.enviarBoleto(id, valorInvalido)
        );
    }

    @Test
    void gerarIdAsaas_PrefixoValido_FormatoCorreto() {
        String id = asaasClient.generateAsaasId(AsaasClient.ASAAS_PAY_PREFIX);
        assertEquals(AsaasClient.ASAAS_PAY_PREFIX.length() + 8, id.length());
    }
}