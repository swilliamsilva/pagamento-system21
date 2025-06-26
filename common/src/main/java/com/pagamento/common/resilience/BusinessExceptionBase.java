package com.pagamento.common.resilience;

import java.util.Map;
import lombok.Getter;

@Getter
public abstract class BusinessExceptionBase extends RuntimeException implements BusinessException {
    
    private final String errorCode;
    private final Object details;

    // Construtores encadeados corretamente
    protected BusinessExceptionBase(String message) {
        this(message, null, null, null);
    }

    protected BusinessExceptionBase(String message, String errorCode) {
        this(message, errorCode, null, null);
    }

    protected BusinessExceptionBase(String message, Throwable cause) {
        this(message, null, null, cause);
    }

    protected BusinessExceptionBase(String message, String errorCode, Throwable cause) {
        this(message, errorCode, null, cause);
    }

    // Construtor principal
    protected BusinessExceptionBase(String message, String errorCode, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    // Construtor específico para Map
    public BusinessExceptionBase(String message, String errorCode, Map<String, Object> details) {
        this(message, errorCode, details, null);
    }
}