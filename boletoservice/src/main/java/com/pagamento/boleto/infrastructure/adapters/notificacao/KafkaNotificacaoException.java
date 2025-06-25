package com.pagamento.boleto.infrastructure.adapters.notificacao;

public class KafkaNotificacaoException extends RuntimeException {
    public KafkaNotificacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}