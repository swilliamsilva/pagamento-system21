package com.pagamento.card.service;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;
import com.pagamento.card.model.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    public boolean processarPagamentoCartao(Card card) {
        logger.info("Processando pagamento para o cartão: {}...", card.maskCardNumberForLog());
        // Lógica simulada de pagamento
        return simularProcessamentoPagamento(card);
    }
    
    private boolean simularProcessamentoPagamento(Card card) {
        // Simulação de processamento com regras básicas:
        // 1. Cartões que terminam com número par são aprovados
        // 2. Cartões que terminam com número ímpar são recusados
        // 3. Cartões com CVV "000" geram erro
        
        String lastDigit = card.getNumero().substring(card.getNumero().length() - 1);
        int digit = Integer.parseInt(lastDigit);
        
        if ("000".equals(card.getCvv())) {
            logger.error("Erro no processamento do cartão: CVV inválido");
            return false;
        }
        
        boolean aprovado = digit % 2 == 0;
        String status = aprovado ? "APROVADO" : "RECUSADO";
        
        logger.info("Pagamento {} para o cartão: {}", status, card.maskCardNumberForLog());
        return aprovado;
    }
    
 // Dentro do seu Service (hexagonal core)
    public CardResponseDTO processarPagamento(CardRequestDTO request) {
        Transaction transacao = paymentProcessor.process(request);
        return new CardResponseDTO(
            transacao.getId(),
            transacao.getBandeira(),
            transacao.getStatus(),
            transacao.getValor(),
            transacao.getAuthCode()
        );
    }
    
}