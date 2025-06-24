// PaymentMapperTest.java
class PaymentMapperTest {
    
    private final PaymentMapper mapper = new PaymentMapper();
    
    @Test
    void shouldMapToEntityCorrectly() {
        Payment entity = mapper.toEntity("user123", "PIX", new BigDecimal("150.00"));
        
        assertNotNull(entity.getTransactionId());
        assertEquals("user123", entity.getUserId());
        assertEquals("PIX", entity.getPaymentType());
        assertEquals(0, new BigDecimal("150.00").compareTo(entity.getAmount()));
        assertNotNull(entity.getCreatedAt());
    }
    
    @Test
    void shouldThrowOnInvalidAmount() {
        assertThrows(IllegalArgumentException.class, 
            () -> mapper.toEntity("user123", "PIX", null));
    }
}