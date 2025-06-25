package com.pagamento.boletoservice;

import java.time.LocalDate;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.domain.ports.BoletoServicePort;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class BoletoServiceIntegrationTest {

    @Autowired
    private BoletoServicePort boletoService;

    @Autowired
    private BoletoRepositoryPort repository;

    @Test
    @Transactional
    void deveEmitirConsultarECancelarBoleto() {
        // Emitir
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A", LocalDate.now()
        );
        Boleto boleto = boletoService.emitirBoleto(request);
        
        // Consultar
        Boleto consultado = boletoService.consultarBoleto(boleto.getId());
        assertNotNull(consultado);
        assertEquals(BoletoStatus.EMITIDO, consultado.getStatus());
        
        // Cancelar
        Boleto cancelado = boletoService.cancelarBoleto(boleto.getId());
        assertEquals(BoletoStatus.CANCELADO, cancelado.getStatus());
    }

    private void assertEquals(BoletoStatus cancelado, BoletoStatus status) {
		// TODO Auto-generated method stub
		
	}

	private void assertNotNull(Boleto consultado) {
		// TODO Auto-generated method stub
		
	}

	@Test
    void deveGerarPDFECodigos() {
        // Emitir boleto
        BoletoRequestDTO request = new BoletoRequestDTO(null, null, null, null);
        Boleto boleto = boletoService.emitirBoleto(request);
        
        // Gerar PDF
        byte[] pdf = boletoService.gerarPDF(boleto.getId());
        assertTrue(pdf.length > 0);
        
        // Gerar códigos
        String codigoBarras = boletoService.gerarCodigoBarras(boleto.getId());
        assertNotNull(codigoBarras);
        
        String qrCode = boletoService.gerarQRCode(boleto.getId());
        assertNotNull(qrCode);
    }

	private void assertNotNull(String codigoBarras) {
		// TODO Auto-generated method stub
		
	}

	private void assertTrue(boolean b) {
		// TODO Auto-generated method stub
		
	}
}