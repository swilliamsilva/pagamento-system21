package com.pagamento.card.interfaces.controller;

import com.pagamento.card.application.service.PaymentService;
import com.pagamento.card.application.dto.*;
import com.pagamento.core.common.exception.PaymentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<CardResponseDTO> processPayment(@RequestBody CardRequestDTO request) {
        return ResponseEntity.ok(paymentService.processarPagamento(request));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex) {
        ErrorResponse error = new ErrorResponse(
            "PAYMENT_ERROR",
            ex.getMessage(),
            System.currentTimeMillis()
        );
        return ResponseEntity.status(500).body(error);
    }
}

// Core Common
