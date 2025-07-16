package com.pagamento.pix.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.pagamento.pix.application.dto.PixRequestDTO;
import com.pagamento.pix.application.dto.PixResponseDTO;
import com.pagamento.pix.domain.model.ChavePix;
import com.pagamento.pix.domain.model.Participante;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.model.PixStatus;

class PixMapperTest {

    @Test
    void toDomain_deveMapearCorretamente() {
        PixRequestDTO dto = new PixRequestDTO();
        dto.setChaveDestino("teste@pagamento.com");
        dto.setTipo("EMAIL");
        dto.setValor(new BigDecimal("150.99"));
        dto.setDocumentoPagador("12345678909");
        dto.setNomePagador("Fulano de Tal");
        dto.setNomeRecebedor("Empresa XYZ");
        dto.setIspbRecebedor("12345678");
        dto.setAgenciaRecebedor("0001");
        dto.setContaRecebedor("12345-6");
        
        Pix pix = PixMapper.toDomain(dto);
        
        assertNotNull(pix);
        assertEquals("teste@pagamento.com", pix.getChaveDestino().getValor());
        assertEquals("EMAIL", pix.getTipo());
        assertEquals(new BigDecimal("150.99"), pix.getValor());
        assertNotNull(pix.getDataTransacao());
        assertEquals("12345678909", pix.getPagador().getDocumento());
        assertEquals("Fulano de Tal", pix.getPagador().getNome());
        assertEquals("Empresa XYZ", pix.getRecebedor().getNome());
        assertEquals("12345678", pix.getRecebedor().getIspb());
        assertEquals("0001", pix.getRecebedor().getAgencia());
        assertEquals("12345-6", pix.getRecebedor().getConta());
    }

    @Test
    void toDomain_deveLancarExcecaoParaDTONulo() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> PixMapper.toDomain(null)
        );
        assertEquals("PixRequestDTO não pode ser nulo", ex.getMessage());
    }

    @Test
    void toResponseDTO_deveMapearCorretamente() {
        Pix pix = new Pix();
        pix.setId("PIX-12345");
        pix.setChaveDestino(new ChavePix("destino@pagamento.com"));
        pix.setTipo("EMAIL");
        pix.setValor(new BigDecimal("150.99"));
        pix.setDataTransacao(LocalDateTime.now());
        pix.setStatus(PixStatus.PROCESSADO.name());
        pix.setBacenId("BID-12345");
        
        Participante recebedor = new Participante();
        recebedor.setNome("Empresa XYZ");
        recebedor.setIspb("12345678");
        recebedor.setAgencia("0001");
        recebedor.setConta("12345-6");
        pix.setRecebedor(recebedor);
        
        PixResponseDTO dto = PixMapper.toResponseDTO(pix);
        
        assertNotNull(dto);
        assertEquals("PIX-12345", dto.getId());
        assertEquals("destino@pagamento.com", dto.getChaveDestino());
        assertEquals("EMAIL", dto.getTipo());
        assertEquals(new BigDecimal("150.99"), dto.getValor());
        assertEquals(pix.getDataTransacao(), dto.getDataTransacao());
        assertEquals("PROCESSADO", dto.getStatus());
        assertEquals("BID-12345", dto.getBacenId());
        assertEquals("Empresa XYZ", dto.getNomeRecebedor());
        assertEquals("12345678", dto.getIspbRecebedor());
        assertEquals("0001", dto.getAgenciaRecebedor());
        assertEquals("12345-6", dto.getContaRecebedor());
    }

    @Test
    void toRequestDTO_deveMapearCorretamente() {
        Pix pix = new Pix();
        pix.setChaveDestino(new ChavePix("origem@pagamento.com"));
        pix.setTipo("EMAIL");
        pix.setValor(new BigDecimal("150.99"));
        
        Participante pagador = new Participante();
        pagador.setDocumento("12345678909");
        pagador.setNome("Fulano de Tal");
        pix.setPagador(pagador);
        
        Participante recebedor = new Participante();
        recebedor.setNome("Empresa XYZ");
        recebedor.setIspb("12345678");
        recebedor.setAgencia("0001");
        recebedor.setConta("12345-6");
        pix.setRecebedor(recebedor);
        
        PixRequestDTO dto = PixMapper.toRequestDTO(pix);
        
        assertNotNull(dto);
        assertEquals("origem@pagamento.com", dto.getChaveDestino());
        assertEquals("EMAIL", dto.getTipo());
        assertEquals(new BigDecimal("150.99"), dto.getValor());
        assertEquals("12345678909", dto.getDocumentoPagador());
        assertEquals("Fulano de Tal", dto.getNomePagador());
        assertEquals("Empresa XYZ", dto.getNomeRecebedor());
        assertEquals("12345678", dto.getIspbRecebedor());
        assertEquals("0001", dto.getAgenciaRecebedor());
        assertEquals("12345-6", dto.getContaRecebedor());
    }

    @Test
    void builder_deveConstruirPixCorretamente() {
        PixRequestDTO dto = new PixRequestDTO();
        dto.setChaveDestino("builder@test.com");
        dto.setTipo("EMAIL");
        dto.setValor(BigDecimal.TEN);
        dto.setDocumentoPagador("11122233344");
        dto.setNomePagador("Builder Test");
        
        Participante recebedor = new Participante();
        recebedor.setNome("Recebedor Builder");
        recebedor.setIspb("87654321");
        
        Pix pix = PixMapper.from(dto)
                .withRecebedor(recebedor)
                .withStatus(PixStatus.PROCESSADO)
                .withTaxa(0.5)
                .build();
        
        assertEquals("builder@test.com", pix.getChaveDestino().getValor());
        assertEquals("Builder Test", pix.getPagador().getNome());
        assertEquals("Recebedor Builder", pix.getRecebedor().getNome());
        assertEquals("87654321", pix.getRecebedor().getIspb());
        assertEquals("PROCESSADO", pix.getStatus());
        assertEquals(0.5, pix.getTaxa());
    }

    @Test
    void toResponseDTO_deveLidarComCamposNulos() {
        Pix pix = new Pix();
        PixResponseDTO dto = PixMapper.toResponseDTO(pix);
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getChaveDestino());
        assertNull(dto.getTipo());
        assertNull(dto.getValor());
        assertNull(dto.getDataTransacao());
        assertNull(dto.getStatus());
        assertNull(dto.getBacenId());
        assertNull(dto.getNomeRecebedor());
        assertNull(dto.getIspbRecebedor());
        assertNull(dto.getAgenciaRecebedor());
        assertNull(dto.getContaRecebedor());
    }
}