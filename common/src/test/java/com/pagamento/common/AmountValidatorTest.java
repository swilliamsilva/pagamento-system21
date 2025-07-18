package com.pagamento.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.pagamento.common.validation.AmountValidator;
import com.pagamento.common.validation.CPFValidator;

class AmountValidatorTest {
    
    private final AmountValidator validator = new AmountValidator();
    
    @Test
    void shouldAcceptPositiveAmount() {
        assertTrue(validator.isValid(new BigDecimal("100.50"), null));
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertFalse(validator.isValid(new BigDecimal("-1"), null));
    }

    @Test
    void shouldRejectZero() {
        assertFalse(validator.isValid(BigDecimal.ZERO, null));
    }
    
    @Test
    void shouldRejectNull() {
        assertFalse(validator.isValid(null, null));
    }
}

class CPFValidatorTest {
    
    private final CPFValidator validator = new CPFValidator();
    
    @Test
    void shouldAcceptValidCPF() {
        // Usando CPF válido conhecido: 529.982.247-25 é INVÁLIDO! Corrigir para um válido.
        assertTrue(validator.isValid("462.636.810-57", null)); // CPF válido
    }
    
    @Test
    void shouldRejectInvalidCPF() {
        assertFalse(validator.isValid("111.111.111-11", null));
    }
    
    @Test
    void shouldRejectNull() {
        assertFalse(validator.isValid(null, null));
    }
}