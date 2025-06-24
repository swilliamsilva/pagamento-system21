package com.pagamento.payment.controller;

import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
import com.pagamento.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamento")
@RequiredArgsConstructor
@Tag(name = "Orquestração de Pagamentos", description = "Gerencia processos de pagamento")
public class PaymentController {

    private final PaymentService paymentService = new PaymentService(null, null, null, null);

    @PostMapping
    @Operation(summary = "Orquestra novo pagamento")
    public ResponseEntity<PaymentResponse> orquestrarPagamento(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processarPagamento(request);
        return ResponseEntity.ok(response);
    }
}