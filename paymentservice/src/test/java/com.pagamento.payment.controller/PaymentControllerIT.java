// PaymentControllerIT.java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PaymentControllerIT {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PaymentService paymentService;
    
    @Test
    void shouldProcessPayment() throws Exception {
        when(paymentService.process(any())).thenReturn(new PaymentResponse("TX123", "SUCCESS"));
        
        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\":\"user1\", \"valor\":100 }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.transactionId").value("TX123"));
    }
}