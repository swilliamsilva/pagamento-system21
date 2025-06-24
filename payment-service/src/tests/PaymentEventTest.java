
// PaymentEventTest.java


class PaymentEventTest {
    
    @Test
    void shouldCreateEventWithDefaultStatus() {
        PaymentEvent event = new PaymentEvent(
            "TX123", 
            "PIX", 
            new BigDecimal("100.00"), 
            Instant.now()
        );
        
        assertEquals("PROCESSADO", event.status());
    }
    
    @Test
    void shouldCreateEventWithCustomStatus() {
        PaymentEvent event = new PaymentEvent(
            "TX124", 
            "BOLETO", 
            new BigDecimal("200.00"), 
            Instant.now(), 
            "APROVADO"
        );
        
        assertEquals("APROVADO", event.status());
    }
}
