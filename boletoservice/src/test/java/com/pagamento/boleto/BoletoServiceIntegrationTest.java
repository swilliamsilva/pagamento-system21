package com.pagamento.boleto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.domain.ports.BoletoServicePort;
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BoletoServiceIntegrationTest {

    @Autowired
    private BoletoServicePort boletoService;

    @Autowired
    private BoletoRepositoryPort repository;

    @Test
    void deveEmitirConsultarECancelarBoleto() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A",
            "123.456.789-00",
            BigDecimal.valueOf(100.00),
            LocalDate.now().plusDays(5)
        );

        Boleto boleto = boletoService.emitirBoleto(request);

        Boleto consultado = boletoService.consultarBoleto(boleto.getId());
        assertNotNull(consultado);
        assertEquals(BoletoStatus.EMITIDO, consultado.getStatus());

        Boleto cancelado = boletoService.cancelarBoleto(boleto.getId());
        assertEquals(BoletoStatus.CANCELADO, cancelado.getStatus());
    }

    @Test
    void deveGerarPDFECodigos() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente B",
            "987.654.321-00",
            BigDecimal.valueOf(200.00),
            LocalDate.now().plusDays(10)
        );

        Boleto boleto = boletoService.emitirBoleto(request);

        byte[] pdf = boletoService.gerarPDF(boleto.getId());
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);

        String codigoBarras = boletoService.gerarCodigoBarras(boleto.getId());
        assertNotNull(codigoBarras);
        assertFalse(codigoBarras.isEmpty());

        String qrCode = boletoService.gerarQRCode(boleto.getId());
        assertNotNull(qrCode);
        assertFalse(qrCode.isEmpty());
    }
}