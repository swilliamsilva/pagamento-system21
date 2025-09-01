package com.pagamento.boleto.exception;

import com.pagamento.common.resilience.BusinessExceptionBase;
import java.util.Map;

public class BoletoValidationException extends BusinessExceptionBase {
    public BoletoValidationException(String message) {
        super(message);
    }

    public BoletoValidationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public BoletoValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoletoValidationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

    public BoletoValidationException(String message, String errorCode, Map<String, Object> details) {
        super(message, errorCode, details);
    }
}


