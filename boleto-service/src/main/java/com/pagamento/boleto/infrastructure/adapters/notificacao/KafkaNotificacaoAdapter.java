/* ========================================================
# Classe: KafkaNotificacaoAdapter
# Módulo: boleto-service (Infraestrutura)
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Adapter para enviar notificações via Kafka.
# ======================================================== */

package com.pagamento.boleto.infrastructure.adapters.notificacao;

import com.pagamento.boleto.domain.ports.NotificacaoPort;
import org.springframework.stereotype.Component;

@Component
public class KafkaNotificacaoAdapter implements NotificacaoPort {

    @Override
    public void enviarNotificacao(String mensagem) {
        System.out.println("📢 Enviando notificação Kafka: " + mensagem);
        // Aqui você poderia usar KafkaTemplate ou qualquer lib real de mensageria
    }
}
