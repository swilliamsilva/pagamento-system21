package com.pagamento.common.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CPFValidatorTest {
    
    private final CPFValidator validator = new CPFValidator();

    @Test
    void shouldAcceptValidCPF() {
        // CPFs válidos conhecidos
        assertTrue(validator.isValid("111.444.777-35", null));
        assertTrue(validator.isValid("11144477735", null));
        assertTrue(validator.isValid("529.982.247-25", null));
        assertTrue(validator.isValid("52998224725", null));
    }

    @Test
    void shouldRejectInvalidCPFs() {
        assertFalse(validator.isValid("123.456.789-10", null)); // Dígito verificador incorreto
        assertFalse(validator.isValid("11111111111", null)); // Sequência repetida
        assertFalse(validator.isValid("00000000000", null)); // Sequência repetida
        assertFalse(validator.isValid("1234567890", null)); // tamanho menor
        assertFalse(validator.isValid("123456789012", null)); // tamanho maior
        assertFalse(validator.isValid("12A.456.789-09", null)); // Caracteres inválidos
    }

    @Test
    void shouldHandleNullCPF() {
        // CPF nulo
        assertFalse(validator.isValid(null, null));
    }
}

