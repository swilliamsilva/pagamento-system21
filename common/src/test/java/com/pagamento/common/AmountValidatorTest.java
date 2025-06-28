package com.pagamento.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.pagamento.common.validation.AmountValidator;
import com.pagamento.common.validation.CPFValidator;

// AmountValidatorTest.java
class AmountValidatorTest {
    
    private final AmountValidator validator = new AmountValidator();
    
    @Test
    void shouldAcceptPositiveAmount() {
        assertTrue(validator.isValid(new BigDecimal("100.50"), null));
    }
    
    private void assertTrue(Object valid) {
		// TODO Auto-generated method stub
		
	}

	@Test
    void shouldRejectNegativeAmount() {
        assertFalse(validator.isValid(new BigDecimal("-1"), null));
    }
    
    private void assertFalse(Object valid) {
		// TODO Auto-generated method stub
		
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

// CPFValidatorTest.java
class CPFValidatorTest {
    
    private final CPFValidator validator = new CPFValidator();
    
    @Test
    void shouldAcceptValidCPF() {
        assertTrue(validator.isValid("529.982.247-25", null));
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