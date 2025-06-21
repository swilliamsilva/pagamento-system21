// ==========================
// TEST: PaymentControllerTest.java
// ==========================
package com.pagamento.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.common.dto.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void deveAceitarPagamentoValido() throws Exception {
        PaymentRequest request = new PaymentRequest("user123", "PIX", new BigDecimal("150.00"));

        mockMvc.perform(post("/api/pagamentos")
                .contentType("application/json")
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
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
