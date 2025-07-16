package com.pagamento.pix.domain.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentoValidatorTest {

    // CPFs válidos
    private static final String CPF_VALIDO_1 = "52998224725";
    private static final String CPF_VALIDO_2 = "45317828790";
    
    // CPFs inválidos
    private static final String CPF_INVALIDO_1 = "11111111111";
    private static final String CPF_INVALIDO_2 = "12345678900";
    
    // CNPJs válidos
    private static final String CNPJ_VALIDO_1 = "11222333000144";
    private static final String CNPJ_VALIDO_2 = "87114952000106";
    
    // CNPJs inválidos
    private static final String CNPJ_INVALIDO_1 = "11222333000141";
    private static final String CNPJ_INVALIDO_2 = "11111111000191";

    @Test
    void deveValidarCPFCorreto() {
        assertTrue(DocumentoValidator.validarCPF(CPF_VALIDO_1));
        assertTrue(DocumentoValidator.validarCPF(CPF_VALIDO_2));
    }

    @Test
    void deveRejeitarCPFInvalido() {
        assertFalse(DocumentoValidator.validarCPF(CPF_INVALIDO_1));
        assertFalse(DocumentoValidator.validarCPF(CPF_INVALIDO_2));
    }

    @Test
    void deveRejeitarCPFComTamanhoErrado() {
        assertFalse(DocumentoValidator.validarCPF("1234567890")); // 10 dígitos
        assertFalse(DocumentoValidator.validarCPF("123456789012")); // 12 dígitos
    }

    @Test
    void deveRejeitarCPFComCaracteresInvalidos() {
        assertFalse(DocumentoValidator.validarCPF("123.456.789-00"));
    }

    @Test
    void deveValidarCNPJCorreto() {
        assertTrue(DocumentoValidator.validarCNPJ(CNPJ_VALIDO_1));
        assertTrue(DocumentoValidator.validarCNPJ(CNPJ_VALIDO_2));
    }

    @Test
    void deveRejeitarCNPJInvalido() {
        assertFalse(DocumentoValidator.validarCNPJ(CNPJ_INVALIDO_1));
        assertFalse(DocumentoValidator.validarCNPJ(CNPJ_INVALIDO_2));
    }

    @Test
    void deveRejeitarCNPJComTamanhoErrado() {
        assertFalse(DocumentoValidator.validarCNPJ("1122233300014")); // 13 dígitos
        assertFalse(DocumentoValidator.validarCNPJ("112223330001441")); // 15 dígitos
    }
}