package common.src.test.java.com.pagamento.common.mapper;

import java.util.Collections;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.pagamento.common.messaging.KafkaTopics;
import com.pagamento.common.messaging.PaymentEvent;

import ch.qos.logback.core.util.Duration;

// KafkaMessagingIT.java
@SpringBootTest
@EmbeddedKafka
class KafkaMessagingIT {
    
    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    
    @Autowired
    private ConsumerFactory<String, PaymentEvent> consumerFactory;
    
    @Test
    void shouldSendAndReceivePaymentEvent() {
        PaymentEvent event = new PaymentEvent("TX123", "COMPLETED");
        
        kafkaTemplate.send(KafkaTopics.PAYMENT_TOPIC, event);
        
        Consumer<String, PaymentEvent> consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton(KafkaTopics.PAYMENT_TOPIC));
        
        ConsumerRecords<String, PaymentEvent> records = consumer.poll(Duration.ofSeconds(5));
        assertEquals(1, records.count());
        assertEquals("TX123", records.iterator().next().value().getTransactionId());
    }
}