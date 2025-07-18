package com.pagamento.boleto.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BoletoStatusHistoricoTest {

    @Test
    void testCriacaoHistoricoValido() {
        Boleto boleto = mock(Boleto.class);
        LocalDateTime dataHora = LocalDateTime.now();
        BoletoStatus status = BoletoStatus.EMITIDO;
        
        BoletoStatusHistorico historico = new BoletoStatusHistorico(status, dataHora, boleto);
        
        assertNotNull(historico);
        assertEquals(status, historico.getStatus());
        assertEquals(dataHora, historico.getDataHora());
        assertEquals(boleto, historico.getBoleto());
    }

    @Test
    void testCriacaoComStatusNuloDeveLancarExcecao() {
        Boleto boleto = mock(Boleto.class);
        LocalDateTime dataHora = LocalDateTime.now();
        
        assertThrows(NullPointerException.class, () -> 
            new BoletoStatusHistorico(null, dataHora, boleto)
        );
    }

    @Test
    void testCriacaoComDataHoraNulaDeveLancarExcecao() {
        Boleto boleto = mock(Boleto.class);
        BoletoStatus status = BoletoStatus.EMITIDO;
        
        assertThrows(NullPointerException.class, () -> 
            new BoletoStatusHistorico(status, null, boleto)
        );
    }

    @Test
    void testCriacaoComBoletoNuloDeveLancarExcecao() {
        LocalDateTime dataHora = LocalDateTime.now();
        BoletoStatus status = BoletoStatus.EMITIDO;
        
        assertThrows(NullPointerException.class, () -> 
            new BoletoStatusHistorico(status, dataHora, null)
        );
    }

    @Test
    void testEqualsAndHashCode() {
        Boleto boleto = mock(Boleto.class);
        LocalDateTime dataHora = LocalDateTime.now();
        
        BoletoStatusHistorico historico1 = new BoletoStatusHistorico(
            BoletoStatus.PAGO, dataHora, boleto
        );
        historico1.setId(1L);
        
        BoletoStatusHistorico historico2 = new BoletoStatusHistorico(
            BoletoStatus.CANCELADO, dataHora.minusDays(1), boleto
        );
        historico2.setId(1L);
        
        BoletoStatusHistorico historico3 = new BoletoStatusHistorico(
            BoletoStatus.PAGO, dataHora, boleto
        );
        historico3.setId(2L);
        
        assertEquals(historico1, historico2);
        assertEquals(historico1.hashCode(), historico2.hashCode());
        assertNotEquals(historico1, historico3);
    }

    @Test
    void testToString() {
        Boleto boleto = mock(Boleto.class);
        LocalDateTime dataHora = LocalDateTime.of(2023, 10, 5, 14, 30);
        BoletoStatusHistorico historico = new BoletoStatusHistorico(
            BoletoStatus.VENCIDO, dataHora, boleto
        );
        historico.setId(99L);
        
        String expected = "BoletoStatusHistorico{" +
                "id=99" +
                ", status=VENCIDO" +
                ", dataHora=2023-10-05T14:30" +
                '}';
        
        assertEquals(expected, historico.toString());
    }

    @Test
    void testSettersProtegidos() {
        Boleto boleto = mock(Boleto.class);
        BoletoStatusHistorico historico = new BoletoStatusHistorico(
            BoletoStatus.EMITIDO, LocalDateTime.now(), boleto
        );
        
        // Deve ser possível via reflection ou pelo JPA
        historico.setId(100L);
        historico.setStatus(BoletoStatus.PAGO);
        historico.setDataHora(LocalDateTime.now().plusDays(1));
        
        assertEquals(100L, historico.getId());
        assertEquals(BoletoStatus.PAGO, historico.getStatus());
    }
}