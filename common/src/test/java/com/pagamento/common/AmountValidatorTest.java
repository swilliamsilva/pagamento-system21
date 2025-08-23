package com.pagamento.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.pagamento.common.validation.AmountValidator;

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


