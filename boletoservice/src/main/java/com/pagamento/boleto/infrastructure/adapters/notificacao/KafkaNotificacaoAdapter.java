/* ========================================================
# Classe: KafkaNotificacaoAdapter
# Módulo: boleto-service (Infraestrutura)
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Adapter para enviar notificações via Kafka.
# ======================================================== */

package com.pagamento.boleto.infrastructure.adapters.notificacao;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.NotificacaoPort;
import com.pagamento.boleto.infrastructure.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public abstract class KafkaNotificacaoAdapter implements NotificacaoPort {

    private static final Logger logger = LoggerFactory.getLogger(KafkaNotificacaoAdapter.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    public KafkaNotificacaoAdapter(KafkaTemplate<String, String> kafkaTemplate) {
    	/**Resource	Date	Description
KafkaNotificacaoAdapter.java	16 days ago	Change the visibility of this constructor to "protected". [+1 location]
**/
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void notificarEmissao(Boleto boleto) {
        String mensagem = String.format(
            "Boleto emitido: ID %s, Valor R$%.2f, Vencimento %s", 
            boleto.getId(), 
            boleto.getValor(), 
            boleto.getDataVencimento()
        );
        enviarMensagem(KafkaConfig.TOPICO_EMISSAO_BOLETO, mensagem);
    }

    @Override
    public void notificarPagamento(Boleto boleto) {
        String mensagem = String.format(
            "Boleto pago: ID %s, Valor R$%.2f, Data Pagamento %s", 
            boleto.getId(), 
            boleto.getValor(), 
            boleto.getDataPagamento()
        );
        enviarMensagem(KafkaConfig.TOPICO_PAGAMENTO_BOLETO, mensagem);
    }

    @Override
    public void notificarCancelamento(Boleto boleto) {
        String mensagem = String.format(
            "Boleto cancelado: ID %s, Motivo: %s", 
            boleto.getId(), 
            boleto.getMotivoCancelamento()
        );
        enviarMensagem(KafkaConfig.TOPICO_CANCELAMENTO_BOLETO, mensagem);
    }

    @Override
    public void notificarReemissao(Boleto original, Boleto reemissao) {
        String mensagem = String.format(
            "Boleto reemitido: Original ID %s, Novo ID %s", 
            original.getId(), 
            reemissao.getId()
        );
        enviarMensagem(KafkaConfig.TOPICO_REEMISSAO_BOLETO, mensagem);
    }
    
    private void enviarMensagem(String topico, String mensagem) {
        try {
            kafkaTemplate.send(topico, mensagem);
            logger.info("Mensagem enviada para o tópico {}: {}", topico, mensagem);
        } catch (Exception e) {
        	/**
        	 * 
        	 * Resource	Date	Description
KafkaNotificacaoAdapter.java	16 days ago	Either log this exception and handle it, or rethrow it with some contextual information. [+2 locations]

        	 * 
        	 * **/
        	
        	
            logger.error("Falha ao enviar mensagem para o Kafka. Tópico: {}, Mensagem: {}", topico, mensagem, e);
            throw new KafkaNotificacaoException("Erro ao enviar mensagem para Kafka", e);
        }
    }
}