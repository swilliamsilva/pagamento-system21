/* ========================================================
# Classe: CardController
# Módulo: card-service (Camada de Aplicação)
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Controller REST para operações com cartão.
# ======================================================== */

package com.pagamento.card.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/cartao")
public class CardController {

    // TODO: Injetar CardService

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Card Service está online");
    }
}
