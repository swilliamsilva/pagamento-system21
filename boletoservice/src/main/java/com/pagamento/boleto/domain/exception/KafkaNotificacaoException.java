package com.pagamento.boleto.domain.exception;

public class KafkaNotificacaoException extends RuntimeException {
    public KafkaNotificacaoException(String message) {
        super(message);
    }

    public KafkaNotificacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}