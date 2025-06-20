/* ========================================================
# Classe: PaymentController
# Módulo: payment-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Exposição de endpoints para orquestração de pagamentos.
# ======================================================== */

package com.pagamento.payment.controller;

import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
import com.pagamento.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamento")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> orquestrarPagamento(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processarPagamento(request);
        return ResponseEntity.ok(response);
    }
}
