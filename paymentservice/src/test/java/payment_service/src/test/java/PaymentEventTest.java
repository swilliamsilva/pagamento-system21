package payment_service.src.test.java;
// PaymentEventTest.java

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Test;

import com.pagamento.common.messaging.PaymentEvent;

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
