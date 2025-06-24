package com.pagamento.payment.controller;

import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
import com.pagamento.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void deveRetornar200_QuandoPagamentoValido() throws Exception {
        PaymentRequest request = new PaymentRequest("PIX", 100.0);
        PaymentResponse response = new PaymentResponse("Sucesso", "123", 100.0, null);
        
        Mockito.when(paymentService.processarPagamento(request)).thenReturn(response);
        
        mockMvc.perform(post("/api/pagamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"PIX\",\"valor\":100.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Sucesso"));
    }
}