// ==========================
// CONTROLLER: PaymentController.java
// ==========================
package com.pagamento.common.controller;

import com.pagamento.common.dto.PaymentRequest;
import com.pagamento.common.dto.PaymentResponse;
import com.pagamento.common.mapper.PaymentMapper;
import com.pagamento.common.model.Payment;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
public class PaymentController {

    @PostMapping
    public ResponseEntity<PaymentResponse> criarPagamento(@RequestBody @Valid PaymentRequest request) {
        Payment entity = PaymentMapper.toEntity(request);
        PaymentResponse response = PaymentMapper.toResponse(entity);
        return ResponseEntity.ok(response);
    }
}