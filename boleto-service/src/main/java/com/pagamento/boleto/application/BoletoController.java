/* ========================================================
# Classe: BoletoController
# Módulo: boleto-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Camada de entrada (application) para o serviço de boletos.
# ======================================================== */

package com.pagamento.boleto.application;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.application.dto.BoletoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boleto")
public class BoletoController {

    @PostMapping
    public ResponseEntity<BoletoResponseDTO> gerarBoleto(@RequestBody BoletoRequestDTO request) {
        // TODO: Chamar serviço de domínio (via port)
        BoletoResponseDTO response = new BoletoResponseDTO("123456789", request.valor(), "boleto gerado com sucesso");
        return ResponseEntity.ok(response);
    }
}
