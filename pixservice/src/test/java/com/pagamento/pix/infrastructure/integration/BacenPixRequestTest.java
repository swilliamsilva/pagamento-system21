package com.pagamento.pix.infrastructure.integration;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BacenPixRequestTest {

    @Test
    void deveCriarRequestCompleta() {
        BacenPixRequest request = new BacenPixRequest();
        
        // Criar pagador
        BacenPixRequest.Participante pagador = new BacenPixRequest.Participante();
        pagador.setCpf("12345678909");
        pagador.setNome("João Silva");
        pagador.setIspb("12345678");
        pagador.setAgencia("0001");
        pagador.setConta("12345-6");
        
        // Criar recebedor
        BacenPixRequest.Participante recebedor = new BacenPixRequest.Participante();
        recebedor.setCpf("98765432100");
        recebedor.setNome("Maria Souza");
        recebedor.setIspb("87654321");
        recebedor.setAgencia("0002");
        recebedor.setConta("65432-1");
        
        // Configurar request
        request.setEndToEndId("E123456789");
        request.setValor(new BigDecimal("150.99"));
        request.setTipo("PIX");
        request.setChave("chave@recebedor.com");
        request.setDataHora(LocalDateTime.now());
        request.setPagador(pagador);
        request.setRecebedor(recebedor);
        
        // Verificações
        assertEquals("E123456789", request.getEndToEndId());
        assertEquals(new BigDecimal("150.99"), request.getValor());
        assertEquals("PIX", request.getTipo());
        assertEquals("chave@recebedor.com", request.getChave());
        assertNotNull(request.getDataHora());
        
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
        assertDoesNotThrow(() -> {
            request.setEndToEndId(null);
            request.setValor(null);
            request.setTipo(null);
            request.setChave(null);
            request.setDataHora(null);
            request.setPagador(null);
            request.setRecebedor(null);
        });
        
        assertNull(request.getEndToEndId());
        assertNull(request.getValor());
        assertNull(request.getTipo());
        assertNull(request.getChave());
        assertNull(request.getDataHora());
        assertNull(request.getPagador());
        assertNull(request.getRecebedor());
    }
}