// CardController.java
package com.pagamento.card.controller;

import com.pagamento.card.model.Card;
import com.pagamento.card.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartao")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Card Service está online");
    }

    @PostMapping("/pagar")
    public ResponseEntity<String> pagar(@RequestBody Card card) {
        boolean resultado = cardService.processarPagamentoCartao(card);
        return resultado ? ResponseEntity.ok("Pagamento realizado com sucesso!") : ResponseEntity.status(500).body("Erro no pagamento");
    }
}