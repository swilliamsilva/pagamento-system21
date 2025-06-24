// AmountValidatorTest.java
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