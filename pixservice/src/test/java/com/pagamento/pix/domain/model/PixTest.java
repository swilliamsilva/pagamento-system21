package com.pagamento.pix.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PixTest {

    @Test
    void builderDeveCriarPixCorretamente() {
        Participante pagador = new Participante();
        pagador.setDocumento("12345678909");
        
        Pix pix = Pix.builder()
                .id("PIX-123")
                .chaveDestino(new ChavePix("destino@email.com"))
                .valor(new BigDecimal("150.99"))
                .pagador(pagador)
                .tipo("EMAIL")
                .build();
        
        assertEquals("PIX-123", pix.getId());
        assertEquals("destino@email.com", pix.getChaveDestino().getValor());
        assertEquals(new BigDecimal("150.99"), pix.getValor());
        assertEquals(pagador, pix.getPagador());
        assertEquals("EMAIL", pix.getTipo());
        assertEquals(PixStatus.EM_PROCESSAMENTO, pix.getStatus());
        assertNotNull(pix.getDataTransacao());
        assertFalse(pix.getHistoricoEstados().isEmpty());
    }

    @Test
    void builderDeveFalharSemCamposObrigatorios() {
        assertThrows(IllegalStateException.class, () -> Pix.builder().build());
        
        assertThrows(IllegalStateException.class, () -> 
            Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .build()
        );
    }

    @Test
    void deveRegistrarHistoricoDeEstados() {
        Pix pix = Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .valor(BigDecimal.TEN)
                .pagador(new Participante())
                .build();
        
        pix.marcarComoErro("Erro de validação");
        pix.iniciarEstorno();
        pix.falharEstorno("Timeout BACEN");
        
        List<EstadoPix> historico = pix.getHistoricoEstados();
        assertEquals(4, historico.size());
        assertEquals("Erro de validação", historico.get(1).getMotivo());
        assertEquals(PixStatus.ERRO_ESTORNO, historico.get(3).getNovoStatus());
    }

    @Test
    void deveCalcularTaxaComDiferentesEstrategias() {
        Pix pix = Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .valor(new BigDecimal("1000"))
                .pagador(new Participante())
                .build();
        
        // Taxa fixa
        pix.calcularTaxa(new TaxaFixaStrategy(5.0));
        assertEquals(5.0, pix.getTaxa());
        
        // Taxa percentual
        pix.calcularTaxa(new TaxaPercentualStrategy(0.01)); // 1%
        assertEquals(10.0, pix.getTaxa());
        
        // Taxa progressiva
        TaxaStrategy progressiva = new TaxaProgressivaStrategy();
        pix.calcularTaxa(progressiva);
        assertEquals(5.0, pix.getTaxa());
        
        pix.setValor(new BigDecimal("10000"));
        pix.calcularTaxa(progressiva);
        assertEquals(20.0, pix.getTaxa());
    }

    @Test
    void transicoesDeEstadoDevemSerValidadas() {
        Pix pix = Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .valor(BigDecimal.TEN)
                .pagador(new Participante())
                .build();
        
        // Deve permitir estorno apenas quando processado
        pix.setStatus(PixStatus.PROCESSADO);
        pix.iniciarEstorno();
        
        // Não deve permitir confirmar estorno sem iniciar
        pix.setStatus(PixStatus.PROCESSADO);
        assertThrows(IllegalStateException.class, pix::confirmarEstorno);
        
        // Não deve permitir estorno de transação já estornada
        pix.setStatus(PixStatus.ESTORNADO);
        assertThrows(IllegalStateException.class, pix::iniciarEstorno);
    }

    @Test
    void deveLancarExcecoesParaOperacoesInvalidas() {
        Pix pix = new Pix();
        
        assertThrows(IllegalArgumentException.class, () -> pix.setValor(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> pix.setDataTransacao(null));
        assertThrows(IllegalArgumentException.class, () -> pix.setPagador(null));
        assertThrows(IllegalArgumentException.class, () -> pix.calcularTaxa(null));
        
        pix.setStatus(PixStatus.PROCESSADO);
        assertThrows(IllegalArgumentException.class, () -> pix.marcarComoProcessado(null));
        assertThrows(IllegalArgumentException.class, () -> pix.marcarComoProcessado(""));
    }
}