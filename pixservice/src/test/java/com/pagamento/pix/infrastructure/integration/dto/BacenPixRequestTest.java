package com.pagamento.pix.infrastructure.integration.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BacenPixRequestTest {

    @Test
    void deveCriarRequestCompleta() {
        // Criar participante pagador
        BacenPixRequest.Participante pagador = new BacenPixRequest.Participante();
        pagador.setCpf("12345678909");
        pagador.setNome("João Silva");
        pagador.setIspb("12345678");
        pagador.setAgencia("0001");
        pagador.setConta("12345-6");
        
        // Criar participante recebedor
        BacenPixRequest.Participante recebedor = new BacenPixRequest.Participante();
        recebedor.setCpf("98765432100");
        recebedor.setNome("Maria Souza");
        recebedor.setIspb("87654321");
        recebedor.setAgencia("0002");
        recebedor.setConta("65432-1");
        
        // Criar request
        BacenPixRequest request = new BacenPixRequest();
        request.setEndToEndId("PIX-12345");
        request.setValor(new BigDecimal("150.99"));
        request.setPagador(pagador);
        request.setRecebedor(recebedor);
        request.setDataHora(LocalDateTime.now());
        request.setTipo("TRANSFERENCIA");
        request.setChave("chave@destino.com");
        
        // Verificar valores
        assertEquals("PIX-12345", request.getEndToEndId());
        assertEquals(new BigDecimal("150.99"), request.getValor());
        assertNotNull(request.getDataHora());
        assertEquals("TRANSFERENCIA", request.getTipo());
        assertEquals("chave@destino.com", request.getChave());
        
        // Verificar pagador
        assertEquals("12345678909", request.getPagador().getCpf());
        assertEquals("João Silva", request.getPagador().getNome());
        assertEquals("12345678", request.getPagador().getIspb());
        assertEquals("0001", request.getPagador().getAgencia());
        assertEquals("12345-6", request.getPagador().getConta());
        
        // Verificar recebedor
        assertEquals("98765432100", request.getRecebedor().getCpf());
        assertEquals("Maria Souza", request.getRecebedor().getNome());
        assertEquals("87654321", request.getRecebedor().getIspb());
        assertEquals("0002", request.getRecebedor().getAgencia());
        assertEquals("65432-1", request.getRecebedor().getConta());
    }

    @Test
    void deveLidarComCamposNulos() {
        BacenPixRequest request = new BacenPixRequest();
        
        assertNull(request.getEndToEndId());
        assertNull(request.getValor());
        assertNull(request.getPagador());
        assertNull(request.getRecebedor());
        assertNull(request.getDataHora());
        assertNull(request.getTipo());
        assertNull(request.getChave());
        
        // Configurar valores nulos explicitamente
        request.setValor(null);
        request.setDataHora(null);
        request.setTipo(null);
        
        assertNull(request.getValor());
        assertNull(request.getDataHora());
        assertNull(request.getTipo());
    }

    @Test
    void deveCriarParticipanteVazio() {
        BacenPixRequest.Participante participante = new BacenPixRequest.Participante();
        
        assertNull(participante.getCpf());
        assertNull(participante.getNome());
        assertNull(participante.getIspb());
        assertNull(participante.getAgencia());
        assertNull(participante.getConta());
        
        // Configurar valores
        participante.setCpf("11122233344");
        participante.setNome("Fulano");
        participante.setIspb("11223344");
        participante.setAgencia("1234");
        participante.setConta("56789-0");
        
        assertEquals("11122233344", participante.getCpf());
        assertEquals("Fulano", participante.getNome());
        assertEquals("11223344", participante.getIspb());
        assertEquals("1234", participante.getAgencia());
        assertEquals("56789-0", participante.getConta());
    }
}