package com.pagamento.common.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

class CPFValidatorTest {
    
    private final CPFValidator validator = new CPFValidator();

    @Test
    void validCpf() {
        assertTrue(validator.isValid("529.982.247-25", null));
        assertTrue(validator.isValid("52998224725", null));
    }

    @Test
    void invalidCpfs() {
        assertFalse(validator.isValid("123.456.789-00", null));
        assertFalse(validator.isValid("11111111111", null));
        assertFalse(validator.isValid("00000000000", null));
        assertFalse(validator.isValid("1234567890", null)); // tamanho menor
        assertFalse(validator.isValid("123456789012", null)); // tamanho maior
    }

    @Test
    void edgeCases() {
        // CPF válido conhecido
        assertTrue(validator.isValid("397.080.470-00", null));
        
        // CPF inválido com dígitos corretos mas sequência bloqueada
        assertFalse(validator.isValid("22222222222", null));
        
        // CPF com caracteres inválidos
        assertFalse(validator.isValid("12A.456.789-09", null));
    }
}