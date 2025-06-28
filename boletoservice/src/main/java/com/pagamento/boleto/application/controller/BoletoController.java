/* ========================================================
# Classe: BoletoController
# Módulo: boleto-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Controller REST para operações com boletos.
# ======================================================== */

package com.pagamento.boleto.application.controller;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.application.dto.BoletoResponseDTO;
import com.pagamento.boleto.application.mapper.BoletoMapper;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.BoletoServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boletos")
public class BoletoController {

    private final BoletoServicePort boletoService;

    public BoletoController(BoletoServicePort boletoService) {
        this.boletoService = boletoService;
    }

    @PostMapping
    public ResponseEntity<BoletoResponseDTO> emitirBoleto(
        @Valid @RequestBody BoletoRequestDTO request
    ) {
        Boleto boleto = boletoService.emitirBoleto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BoletoMapper.toDTO(boleto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletoResponseDTO> consultarBoleto(@PathVariable String id) {
        Boleto boleto = boletoService.consultarBoleto(id);
        return ResponseEntity.ok(BoletoMapper.toDTO(boleto));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarBoleto(
        @PathVariable String id,
        @RequestParam String motivo
    ) {
        boletoService.cancelarBoleto(id, motivo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reemitir")
    public ResponseEntity<BoletoResponseDTO> reemitirBoleto(@PathVariable String id) {
        Boleto reemissao = boletoService.reemitirBoleto(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(BoletoMapper.toDTO(reemissao));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPdfBoleto(@PathVariable String id) {
        byte[] pdf = boletoService.gerarPDF(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"boleto.pdf\"")
                .body(pdf);
    }

    @GetMapping("/{id}/codigo-barras")
    public ResponseEntity<String> obterCodigoBarras(@PathVariable String id) {
        String codigo = boletoService.gerarCodigoBarras(id);
        return ResponseEntity.ok(codigo);
    }

    @GetMapping("/{id}/qr-code")
    public ResponseEntity<String> obterQrCode(@PathVariable String id) {
        String qrCode = boletoService.gerarQRCode(id);
        return ResponseEntity.ok(qrCode);
    }
}
