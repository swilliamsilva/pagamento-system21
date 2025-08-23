package com.pagamento.pix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PixNotFoundException extends RuntimeException {
    public PixNotFoundException(String message) {
        super(message);
    }
}


