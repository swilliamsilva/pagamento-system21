package com.pagamento.boleto.domain.ports;

public interface NotificacaoPort {
    void enviarNotificacao(String mensagem);
}
