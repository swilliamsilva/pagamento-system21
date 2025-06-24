// CardControllerTest.java (MockMVC)
package com.pagamento.card.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.card.model.Card;
import com.pagamento.card.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
    }

    @Test
    void deveRetornarStatusOk() throws Exception {
        mockMvc.perform(get("/api/cartao/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card Service está online"));
    }

    @Test
    void deveProcessarPagamento() throws Exception {
        Card card = new Card("1", "Maria Souza", "5555666677778888", "11/26", "321");
        when(cardService.processarPagamentoCartao(any(Card.class))).thenReturn(true);

        mockMvc.perform(post("/api/cartao/pagar")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(card)))
                .andExpect(status().isOk())
                .andExpect(content().string("Pagamento realizado com sucesso!"));
    }
}
