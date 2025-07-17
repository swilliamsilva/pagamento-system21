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
        pix.calcularTaxa(new TaxaPercentualStrategy(0.01));
        assertEquals(10.0, pix.getTaxa());
        
        // Taxa progressiva
        TaxaStrategy progressiva = new TaxaProgressivaStrategy();
        pix.calcularTaxa(progressiva);
        assertEquals(5.0, pix.getTaxa());
        
        // Novo pix com valor maior
        Pix pixGrande = Pix.builder()
                .chaveDestino(new ChavePix("teste2"))
                .valor(new BigDecimal("10000"))
                .pagador(new Participante())
                .build();
        pixGrande.calcularTaxa(progressiva);
        assertEquals(20.0, pixGrande.getTaxa());
    }

    @Test
    void transicoesDeEstadoDevemSerValidadas() {
        Pix pix = Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .valor(BigDecimal.TEN)
                .pagador(new Participante())
                .build();
        
        pix.marcarComoProcessado("ID-BACEN");
        pix.iniciarEstorno();
        
        // Tentar confirmar estorno sem iniciar
        Pix pix2 = Pix.builder()
                .chaveDestino(new ChavePix("teste2"))
                .valor(BigDecimal.ONE)
                .pagador(new Participante())
                .build();
        pix2.marcarComoProcessado("ID-BACEN2");
        assertThrows(IllegalStateException.class, pix2::confirmarEstorno);
        
        // Tentar estornar transação já estornada
        pix.confirmarEstorno();
        assertThrows(IllegalStateException.class, pix::iniciarEstorno);
    }

    @Test
    void deveLancarExcecoesParaOperacoesInvalidas() {
        // Teste via builder para validações
        assertThrows(IllegalStateException.class, () -> 
            Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .valor(BigDecimal.ZERO) // Inválido
                .pagador(new Participante())
                .build()
        );
        
        assertThrows(IllegalStateException.class, () -> 
            Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .valor(BigDecimal.TEN)
                .pagador(null) // Inválido
                .build()
        );
        
        Pix pix = Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .valor(BigDecimal.TEN)
                .pagador(new Participante())
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> pix.calcularTaxa(null));
        assertThrows(IllegalArgumentException.class, () -> pix.marcarComoProcessado(null));
        assertThrows(IllegalArgumentException.class, () -> pix.marcarComoProcessado(""));
    }
    
    @Test
    void aoConfirmarEstorno_deveSetarTimestamp() {
        Pix pix = Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .valor(BigDecimal.TEN)
                .pagador(new Participante())
                .build();
        
        pix.marcarComoProcessado("ID-BACEN");
        pix.iniciarEstorno();
        pix.confirmarEstorno();
        
        assertEquals(PixStatus.ESTORNADO, pix.getStatus());
        assertNotNull(pix.getEstornadoEm());
    }
}