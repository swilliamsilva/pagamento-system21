package com.pagamento.pix.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.pagamento.pix.domain.model.Pix.Builder;

class PixTest {

    // Implementação simples das estratégias para os testes (mantido igual)
    static class TaxaFixaStrategy implements TaxaStrategy {
        private final double taxa;
        public TaxaFixaStrategy(double taxa) { this.taxa = taxa; }
        @Override public double calcular(BigDecimal valor) { return taxa; }
    }

    static class TaxaPercentualStrategy implements TaxaStrategy {
        private final double percentual;
        public TaxaPercentualStrategy(double percentual) { this.percentual = percentual; }
        @Override public double calcular(BigDecimal valor) { 
            return valor.multiply(BigDecimal.valueOf(percentual)).doubleValue(); 
        }
    }

    static class TaxaProgressivaStrategy implements TaxaStrategy {
        @Override public double calcular(BigDecimal valor) {
            if (valor.compareTo(new BigDecimal("1000")) <= 0) return 5.0;
            else if (valor.compareTo(new BigDecimal("5000")) <= 0) return 10.0;
            else return 20.0;
        }
    }

    // Método auxiliar para criação de builder com configuração mínima
    private Builder criarBuilderBasico() {
        Participante pagador = new Participante();
        pagador.setDocumento("12345678909");
        
        return Pix.builder()
                .chaveDestino(new ChavePix("teste"))
                .valor(BigDecimal.TEN)
                .pagador(pagador);
    }

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
        // Caso 1: Builder sem nenhum campo
        Pix.Builder builderVazio = Pix.builder();
        assertThrows(IllegalStateException.class, builderVazio::build);
        
        // Caso 2: Builder sem pagador
        Builder builderSemPagador = Pix.builder()
            .chaveDestino(new ChavePix("teste"))
            .valor(BigDecimal.TEN);
            
        assertThrows(IllegalStateException.class, builderSemPagador::build);
    }

    @Test
    void deveRegistrarHistoricoDeEstados() {
        Pix pix = criarBuilderBasico().build();
        
        // Transições de estado
        pix.marcarComoErro("Erro de validação");
        pix.iniciarEstorno("Solicitação do cliente");
        pix.falharEstorno("Timeout BACEN");
        
        List<EstadoPix> historico = pix.getHistoricoEstados();
        assertEquals(4, historico.size());
        assertEquals("Erro de validação", historico.get(1).getMotivo());
        assertEquals(PixStatus.ERRO_ESTORNO, pix.getStatus());
    }

    @Test
    void deveCalcularTaxaComDiferentesEstrategias() {
        Pix pix = criarBuilderBasico().build();
        
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
        Pix pixGrande = criarBuilderBasico()
            .valor(new BigDecimal("10000"))
            .build();
            
        pixGrande.calcularTaxa(progressiva);
        assertEquals(20.0, pixGrande.getTaxa());
    }

    @Test
    void transicoesDeEstadoDevemSerValidadas() {
        Pix pix = criarBuilderBasico().build();
        
        pix.marcarComoProcessado("ID-BACEN");
        pix.iniciarEstorno("Solicitação do cliente");
        
        // Tentar confirmar estorno sem estar no estado ESTORNANDO
        Pix pix2 = criarBuilderBasico().build();
        pix2.marcarComoProcessado("ID-BACEN2");
        assertThrows(IllegalStateException.class, pix2::confirmarEstorno);
        
        // Tentar estornar transação já estornada
        pix.confirmarEstorno();
        assertThrows(IllegalStateException.class, () -> pix.iniciarEstorno("Tentativa inválida"));
    }

    @Test
    void deveLancarExcecoesParaOperacoesInvalidas() {
        // Caso 1: Valor zero
        Builder builderValorZero = criarBuilderBasico()
            .valor(BigDecimal.ZERO);
            
        assertThrows(IllegalArgumentException.class, builderValorZero::build);
        
        // Caso 2: Pagador nulo
        Builder builderPagadorNulo = Pix.builder()
            .chaveDestino(new ChavePix("teste"))
            .valor(BigDecimal.TEN)
            .pagador(null);
            
        assertThrows(IllegalStateException.class, builderPagadorNulo::build);
        
        // Caso 3: Operações inválidas em instância existente
        Pix pix = criarBuilderBasico().build();
        
        assertThrows(IllegalArgumentException.class, () -> pix.calcularTaxa(null));
        assertThrows(IllegalArgumentException.class, () -> pix.marcarComoProcessado(null));
        assertThrows(IllegalArgumentException.class, () -> pix.marcarComoProcessado(""));
        assertThrows(IllegalArgumentException.class, () -> pix.iniciarEstorno(null));
        assertThrows(IllegalArgumentException.class, () -> pix.iniciarEstorno(""));
    }
    
    @Test
    void aoConfirmarEstorno_deveSetarTimestamp() {
        Pix pix = criarBuilderBasico().build();
        
        pix.marcarComoProcessado("ID-BACEN");
        pix.iniciarEstorno("Solicitação do cliente");
        pix.confirmarEstorno();
        
        assertEquals(PixStatus.ESTORNADO, pix.getStatus());
        assertNotNull(pix.getEstornadoEm());
    }
}