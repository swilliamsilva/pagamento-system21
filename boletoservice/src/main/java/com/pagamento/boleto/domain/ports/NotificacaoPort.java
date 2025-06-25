package com.pagamento.boleto.domain.ports;

import com.pagamento.boleto.domain.model.Boleto;

public interface NotificacaoPort {
    void enviarNotificacao(String mensagem);

	void notificarEmissao(Boleto boleto);

	void notificarReemissao(Boleto original, Boleto reemissao);

	void notificarCancelamento(Boleto boleto);

	void notificarPagamento(Boleto boleto);
}
