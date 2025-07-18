package com.pagamento.common.messaging;

/**
 * Define os tópicos Kafka utilizados no sistema de pagamento.
 */
public class KafkaTopics {
    public static final String TOPICO_PAGAMENTO_REALIZADO = "pagamento-realizado";
    public static final String TOPICO_BOLETO_EMITIDO = "boleto-emitido";
    public static final String TOPICO_PIX_CONFIRMADO = "pix-confirmado";
    public static final String TOPICO_CARTAO_APROVADO = "cartao-aprovado";

    private KafkaTopics() {
        // Construtor privado para prevenir instanciação
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }
}