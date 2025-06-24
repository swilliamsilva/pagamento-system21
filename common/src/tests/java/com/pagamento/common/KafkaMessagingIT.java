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