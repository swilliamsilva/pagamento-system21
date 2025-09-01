package com.pagamento.boleto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BoletoNotFoundException extends RuntimeException {
    public BoletoNotFoundException(String message) {
        super(message);
    }
}


