/* ========================================================
# Classe: PixController
# Módulo: pix-service (Camada de Aplicação)
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Controller REST para operações Pix.
# ======================================================== */

package com.pagamento.pix.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/pix")
public class PixController {

    // TODO: Injetar PixService

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Pix Service está online");
    }
}
