// PaymentControllerTest.java
package com.pagamento.common.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.common.request.PaymentRequest;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    public void deveAceitarPagamentoValido() throws Exception {
        PaymentRequest request = new PaymentRequest("user123", "PIX", new BigDecimal("150.00"));

        mockMvc.perform(post("/api/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTransacao").exists())
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.valor").value(150.00));
    }

    @Test
    public void deveRejeitarPagamentoInvalido() throws Exception {
        PaymentRequest request = new PaymentRequest("user123", "PIX", null);

        mockMvc.perform(post("/api/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}