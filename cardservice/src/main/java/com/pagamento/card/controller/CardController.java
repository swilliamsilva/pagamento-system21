package com.pagamento.card.controller;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.model.Card;
import com.pagamento.card.service.CardService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<PaymentResponse> pagar(@RequestBody Card card) {
        boolean resultado = cardService.processarPagamentoCartao(card);
        
        if (resultado) {
            return ResponseEntity.ok(
                new PaymentResponse("Pagamento realizado com sucesso!", true)
            );
        }
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new PaymentResponse("Falha no processamento do pagamento", false));
    }
    
    @PostMapping("/pagar")
    public ResponseEntity<PaymentResponse> pagar(@Valid @RequestBody CardRequestDTO cardDTO) {
        Card card = new Card(
            null, // ID pode ser gerado posteriormente
            cardDTO.getNomeTitular(),
            cardDTO.getNumero(),
            cardDTO.getValidade(),
            cardDTO.getCvv()
        );
        
        boolean resultado = cardService.processarPagamentoCartao(card);
        
        if (resultado) {
            return ResponseEntity.ok(
                new PaymentResponse("Pagamento realizado com sucesso!", true)
            );
        }
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new PaymentResponse("Falha no processamento do pagamento", false));
    }
    
    // DTO para resposta padronizada
    public static record PaymentResponse(String message, boolean success) {}
}