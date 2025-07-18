// ==========================
// CONTROLLER: PaymentController.java
// ==========================
package com.pagamento.common.controller;

import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
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
    
        PaymentMapper mapper = new PaymentMapper();
        Payment entity = mapper.toEntity(request);
        PaymentResponse response = mapper.toResponse(entity);
        return ResponseEntity.ok(response);
        
        
    }
}