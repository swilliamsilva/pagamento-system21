package com.pagamento.common.resilience;

import java.io.Serializable;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class BusinessExceptionBase extends RuntimeException implements BusinessException, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    private final transient Object details; // Campo marcado como transient

    // Construtores melhorados
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

    protected BusinessExceptionBase(String message, String errorCode, Serializable details) {
        this(message, errorCode, details, null);
    }

    protected BusinessExceptionBase(String message, String errorCode, Serializable details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    // Construtor alterado para protected
    protected BusinessExceptionBase(String message, String errorCode, Map<String, Serializable> details) {
        this(message, errorCode, (Serializable) details, null);
    }

    // Método para acesso seguro aos detalhes
    public <T extends Serializable> T getDetailsAs(Class<T> type) {
        return type.isInstance(details) ? type.cast(details) : null;
    }
}