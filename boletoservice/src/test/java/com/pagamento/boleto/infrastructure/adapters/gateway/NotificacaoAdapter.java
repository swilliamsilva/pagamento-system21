package com.pagamento.boleto.infrastructure.adapters.gateway;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.NotificacaoPort;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoAdapter implements NotificacaoPort {

    @Override
    public void enviarNotificacao(String mensagem) {
        System.out.println("Notificação enviada: " + mensagem);
    }

    @Override
    public void notificarEmissao(Boleto boleto) {
        System.out.println("Emissão de boleto: " + boleto.getId());
    }

    @Override
    public void notificarReemissao(Boleto original, Boleto reemissao) {
        System.out.println("Reemissão: " + original.getId() + " -> " + reemissao.getId());
    }

    @Override
    public void notificarCancelamento(Boleto boleto) {
        System.out.println("Cancelamento de boleto: " + boleto.getId());
    }

    @Override
    public void notificarPagamento(Boleto boleto) {
        System.out.println("Pagamento confirmado do boleto: " + boleto.getId());
    }
}
