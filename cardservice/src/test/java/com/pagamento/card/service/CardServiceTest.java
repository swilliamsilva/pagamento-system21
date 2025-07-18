package com.pagamento.card.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import com.pagamento.card.model.Card;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    private CardService cardService;
    
    @Mock
    private Logger logger;

    private Card cartaoValidoPar;
    private Card cartaoValidoImpar;
    private Card cartaoCvvInvalido;

    @BeforeEach
    void setUp() {
        // Injetar logger mockado
        ReflectionTestUtils.setField(cardService, "logger", logger);
        
        cartaoValidoPar = new Card("1", "Maria Silva", "4111111111111112", "12/25", "123");
        cartaoValidoImpar = new Card("2", "João Souza", "4111111111111111", "12/25", "456");
        cartaoCvvInvalido = new Card("3", "Pedro Silva", "4111111111111110", "12/25", "000");
    }

    @Test
    void deveAprovarPagamentoParaCartoesTerminadosComDigitoPar() {
        boolean resultado = cardService.processarPagamentoCartao(cartaoValidoPar);
        assertTrue(resultado);
        
        verify(logger).info("Processando pagamento para o cartão: {}...", "****-****-****-1112");
        verify(logger).info("Pagamento APROVADO para o cartão: {}", "****-****-****-1112");
    }

    @Test
    void deveRecusarPagamentoParaCartoesTerminadosComDigitoImpar() {
        boolean resultado = cardService.processarPagamentoCartao(cartaoValidoImpar);
        assertFalse(resultado);
        
        verify(logger).info("Processando pagamento para o cartão: {}...", "****-****-****-1111");
        verify(logger).info("Pagamento RECUSADO para o cartão: {}", "****-****-****-1111");
    }

    @Test
    void deveRecusarPagamentoParaCartoesComCVV000() {
        boolean resultado = cardService.processarPagamentoCartao(cartaoCvvInvalido);
        assertFalse(resultado);
        
        verify(logger).info("Processando pagamento para o cartão: {}...", "****-****-****-1110");
        verify(logger).error("Erro no processamento do cartão: CVV inválido");
    }

    @Test
    void deveRegistrarLogsAdequadamente() {
        cardService.processarPagamentoCartao(cartaoValidoPar);
        
        verify(logger).info(anyString(), anyString());
        verify(logger, never()).error(anyString());
    }
}