package com.pagamento.pix.application.mapper;

import com.pagamento.pix.application.dto.PixRequestDTO;
import com.pagamento.pix.application.dto.PixResponseDTO;
import com.pagamento.pix.domain.model.ChavePix;
import com.pagamento.pix.domain.model.Participante;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.model.PixStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class PixMapper {

    /**
     * Converte um PixRequestDTO para a entidade Pix de domínio.
     *
     * @param dto dados da requisição
     * @return entidade Pix pronta para processamento
     * @throws IllegalArgumentException se o DTO for nulo
     */
    public static Pix toDomain(PixRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("PixRequestDTO não pode ser nulo");
        }
        
        Pix pix = new Pix();
        
        // Configurar chave destino
        if (dto.getChaveDestino() != null) {
            pix.setChaveDestino(new ChavePix(dto.getChaveDestino()));
        }
        
        pix.setTipo(dto.getTipo());
        pix.setValor(dto.getValor());
        pix.setDataTransacao(LocalDateTime.now());
        
        // Configurar pagador
        Participante pagador = new Participante();
        pagador.setDocumento(dto.getDocumentoPagador());
        pagador.setNome(dto.getNomePagador());
        pix.setPagador(pagador);
        
        // Configurar recebedor, se fornecido
        if (dto.getIspbRecebedor() != null || dto.getAgenciaRecebedor() != null || dto.getContaRecebedor() != null) {
            Participante recebedor = new Participante();
            recebedor.setIspb(dto.getIspbRecebedor());
            recebedor.setAgencia(dto.getAgenciaRecebedor());
            recebedor.setConta(dto.getContaRecebedor());
            // Nome do recebedor pode ser opcional? Vamos considerar que está no DTO
            recebedor.setNome(dto.getNomeRecebedor());
            pix.setRecebedor(recebedor);
        }
        
        return pix;
    }

    /**
     * Converte uma entidade Pix de domínio para PixResponseDTO.
     *
     * @param entity entidade persistida
     * @return DTO de resposta para API
     */
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

    /**
     * Converte uma entidade Pix de domínio para PixRequestDTO.
     * (Útil para operações de atualização ou retorno parcial)
     *
     * @param entity entidade persistida
     * @return DTO de requisição
     */
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

    /**
     * Builder para construção flexível de um Pix a partir de um PixRequestDTO.
     * Permite adicionar informações adicionais após o mapeamento básico.
     */
    public static class PixBuilder {
        private final Pix pix;
        
        public PixBuilder(PixRequestDTO dto) {
            this.pix = toDomain(dto);
        }
        
        public PixBuilder withRecebedor(Participante recebedor) {
            pix.setRecebedor(recebedor);
            return this;
        }
        
        public PixBuilder withStatus(PixStatus status) {
            pix.setStatus(status.name());
            return this;
        }
        
        public PixBuilder withTaxa(double taxa) {
            pix.setTaxa(taxa);
            return this;
        }
        
        public Pix build() {
            return pix;
        }
    }
    
    public static PixBuilder from(PixRequestDTO dto) {
        return new PixBuilder(dto);
    }
}