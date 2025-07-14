package test.com.pagamento.card.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.card.controller.CardController;
import com.pagamento.card.model.Card;
import com.pagamento.card.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest { // Removido modificador 'public'

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    private ObjectMapper mapper;
    private Card validCard;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        validCard = new Card("1", "Maria Souza", "5555666677778888", "11/26", "321");
    }

    @Test
    void deveRetornarStatusOk() throws Exception {
        mockMvc.perform(get("/api/cartao/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card Service está online"));
    }

    @Test
    void deveProcessarPagamentoComSucesso() throws Exception {
        when(cardService.processarPagamentoCartao(any(Card.class))).thenReturn(true);

        mockMvc.perform(post("/api/cartao/pagar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCard)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pagamento realizado com sucesso!"));
    }

    @Test
    void deveRetornarErroAoProcessarPagamento() throws Exception {
        when(cardService.processarPagamentoCartao(any(Card.class))).thenReturn(false);

        mockMvc.perform(post("/api/cartao/pagar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCard)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Falha no processamento do pagamento"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123", "12345678901234567", "abcd"})
    void deveRejeitarCartaoComNumeroInvalido(String numeroInvalido) throws Exception {
        Card invalidCard = new Card("1", "Maria Souza", numeroInvalido, "11/26", "321");
        
        mockMvc.perform(post("/api/cartao/pagar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidCard)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarSemBody() throws Exception {
        mockMvc.perform(post("/api/cartao/pagar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}