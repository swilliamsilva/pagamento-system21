// CardService.java
package com.pagamento.card.service;

import com.pagamento.card.model.Card;
import org.springframework.stereotype.Service;

@Service
public class CardService {
    public boolean processarPagamentoCartao(Card card) {
        System.out.printf(" Processando pagamento para o cartão: %s...%n", card.getNumber());
        return true;
    }
}
