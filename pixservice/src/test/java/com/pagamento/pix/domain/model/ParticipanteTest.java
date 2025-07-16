package com.pagamento.pix.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParticipanteTest {

    @Test
    void deveCriarParticipanteValido() {
        Participante participante = new Participante();
        participante.setNome("João Silva");
        participante.setDocumento("52998224725");
        participante.setIspb("12345678");
        participante.setAgencia("0001");
        participante.setConta("12345-6");
        
        assertEquals("João Silva", participante.getNome());
        assertEquals("52998224725", participante.getDocumento());
        assertEquals("12345678", participante.getIspb());
        assertEquals("0001", participante.getAgencia());
        assertEquals("12345-6", participante.getConta());
    }

    @Test
    void devePermitirDocumentoCNPJ() {
        Participante participante = new Participante();
        participante.setDocumento("11222333000144");
        assertEquals("11222333000144", participante.getDocumento());
    }
}