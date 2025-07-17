package com.pagamento.pix.application.mapper;

import com.pagamento.pix.application.dto.PixRequestDTO;
import com.pagamento.pix.application.dto.PixResponseDTO;
import com.pagamento.pix.domain.model.ChavePix;
import com.pagamento.pix.domain.model.Participante;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.model.PixStatus;

import java.time.LocalDateTime;

public class PixMapper {

    public static Pix toDomain(PixRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("PixRequestDTO não pode ser nulo");
        }
        
        // Criar pagador
        Participante pagador = new Participante();
        pagador.setDocumento(dto.getDocumentoPagador());
        pagador.setNome(dto.getNomePagador());
        
        // Construir Pix usando o Builder original da classe Pix
        Pix.Builder builder = Pix.builder()
                .chaveDestino(new ChavePix(dto.getChaveDestino()))
                .valor(dto.getValor())
                .pagador(pagador)
                .tipo(dto.getTipo())
                .dataTransacao(LocalDateTime.now());
        
        // Adicionar recebedor se disponível
        if (dto.getNomeRecebedor() != null || 
            dto.getIspbRecebedor() != null || 
            dto.getAgenciaRecebedor() != null || 
            dto.getContaRecebedor() != null) {
            
            Participante recebedor = new Participante();
            recebedor.setNome(dto.getNomeRecebedor());
            recebedor.setIspb(dto.getIspbRecebedor());
            recebedor.setAgencia(dto.getAgenciaRecebedor());
            recebedor.setConta(dto.getContaRecebedor());
            builder.recebedor(recebedor);
        }
        
        return builder.build();
    }

    public static PixResponseDTO toResponseDTO(Pix entity) {
        if (entity == null) {
            return null;
        }
        
        PixResponseDTO dto = new PixResponseDTO();
        dto.setId(entity.getId());
        
        if (entity.getChaveDestino() != null) {
            dto.setChaveDestino(entity.getChaveDestino().getValor());
        }
        
        dto.setTipo(entity.getTipo());
        dto.setValor(entity.getValor());
        dto.setDataTransacao(entity.getDataTransacao());
        dto.setStatus(entity.getStatus());
        dto.setBacenId(entity.getBacenId());
        
        if (entity.getRecebedor() != null) {
            dto.setNomeRecebedor(entity.getRecebedor().getNome());
            dto.setIspbRecebedor(entity.getRecebedor().getIspb());
            dto.setAgenciaRecebedor(entity.getRecebedor().getAgencia());
            dto.setContaRecebedor(entity.getRecebedor().getConta());
        }
        
        return dto;
    }

    public static PixRequestDTO toRequestDTO(Pix entity) {
        if (entity == null) {
            return null;
        }
        
        PixRequestDTO dto = new PixRequestDTO();
        if (entity.getChaveDestino() != null) {
            dto.setChaveDestino(entity.getChaveDestino().getValor());
        }
        dto.setTipo(entity.getTipo());
        dto.setValor(entity.getValor());
        
        if (entity.getPagador() != null) {
            dto.setDocumentoPagador(entity.getPagador().getDocumento());
            dto.setNomePagador(entity.getPagador().getNome());
        }
        
        if (entity.getRecebedor() != null) {
            dto.setNomeRecebedor(entity.getRecebedor().getNome());
            dto.setIspbRecebedor(entity.getRecebedor().getIspb());
            dto.setAgenciaRecebedor(entity.getRecebedor().getAgencia());
            dto.setContaRecebedor(entity.getRecebedor().getConta());
        }
        
        return dto;
    }
}