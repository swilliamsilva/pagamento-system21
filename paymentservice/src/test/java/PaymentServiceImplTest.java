
// PaymentServiceImplTest.java
class PaymentServiceImplTest {
    
    @Mock private PaymentRepositoryPort repository;
    @Mock private PaymentMapper mapper;
    @Mock private KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    @Mock private RestTemplate restTemplate;
    
    private PaymentServiceImpl service;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new PaymentServiceImpl(repository, mapper, kafkaTemplate, restTemplate);
    }
    
    @Test
    void shouldProcessPixPayment() {
        PaymentRequest request = new PaymentRequest("user1", "PIX", new BigDecimal("100"));
        Payment payment = new Payment();
        
        when(mapper.toEntity(request)).thenReturn(payment);
        when(repository.salvar(payment)).thenReturn(payment);
        
        service.processarPagamento(request);
        
        verify(restTemplate).postForEntity(eq("http://pix-service/api/pix"), eq(request), eq(Void.class));
        verify(kafkaTemplate).send(eq("pagamento-processado"), any(PaymentEvent.class));
    }
    
    @Test
    void shouldTriggerFallbackOnFailure() {
        PaymentRequest request = new PaymentRequest("user1", "PIX", new BigDecimal("100"));
        
        when(mapper.toEntity(request)).thenThrow(new RuntimeException("Simulated failure"));
        
        PaymentResponse response = service.processarPagamento(request);
        
        assertEquals("FALHA", response.status());
        assertEquals("Serviço temporariamente indisponível", response.mensagem());
    }
}