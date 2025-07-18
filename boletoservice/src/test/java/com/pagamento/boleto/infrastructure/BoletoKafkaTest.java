package com.pagamento.boleto.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.pagamento.boleto.application.dto.PagamentoConfirmadoDTO;
import com.pagamento.boleto.domain.model.BoletoStatus;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@DirtiesContext
@ActiveProfiles("test")
@EnableKafka
class BoletoKafkaTest {

    @Autowired
    private EmbeddedKafkaBroker kafka;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Variáveis para armazenar mensagens recebidas
    private PagamentoConfirmadoDTO receivedPagamento;
    private BoletoStatus receivedStatus;
    private final CountDownLatch latch = new CountDownLatch(1);

    @Test
    void shouldSendAndReceivePagamentoConfirmadoMessage() throws InterruptedException {
        // Arrange
        PagamentoConfirmadoDTO pagamento = new PagamentoConfirmadoDTO(
            UUID.randomUUID().toString(),
            "DOC-123",
            new BigDecimal("1500.00"),
            LocalDate.now()
        );

        // Act
        kafkaTemplate.send("pagamento-confirmado", pagamento);

        // Aguarda até 5 segundos para a mensagem ser processada
        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);

        // Assert
        assertTrue(messageReceived, "Mensagem não recebida a tempo");
        assertNotNull(receivedPagamento);
        assertEquals(pagamento.idBoleto(), receivedPagamento.idBoleto());
        assertEquals(pagamento.documento(), receivedPagamento.documento());
        assertEquals(0, pagamento.valor().compareTo(receivedPagamento.valor()));
    }

    @Test
    void shouldSendAndReceiveCancelamentoMessage() throws InterruptedException {
        // Arrange
        String boletoId = UUID.randomUUID().toString();
        BoletoStatus status = BoletoStatus.CANCELADO;

        // Act
        kafkaTemplate.send("cancelamento", boletoId, status);

        // Aguarda até 5 segundos para a mensagem ser processada
        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);

        // Assert
        assertTrue(messageReceived, "Mensagem não recebida a tempo");
        assertNotNull(receivedStatus);
        assertEquals(status, receivedStatus);
    }

    // Listener para mensagens de pagamento confirmado
    @KafkaListener(topics = "pagamento-confirmado", groupId = "test-group")
    public void handlePagamentoConfirmado(PagamentoConfirmadoDTO pagamento) {
        this.receivedPagamento = pagamento;
        latch.countDown();
    }

    // Listener para mensagens de cancelamento
    @KafkaListener(topics = "cancelamento", groupId = "test-group")
    public void handleCancelamento(BoletoStatus status) {
        this.receivedStatus = status;
        latch.countDown();
    }
}