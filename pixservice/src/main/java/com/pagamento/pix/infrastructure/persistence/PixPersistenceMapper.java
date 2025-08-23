package com.pagamento.pix.infrastructure.persistence;

import com.pagamento.pix.domain.model.ChavePix;
import com.pagamento.pix.domain.model.Participante;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.model.PixStatus;
import com.pagamento.pix.domain.model.TipoChave;
import com.pagamento.pix.repository.dynamo.PixDynamoEntity;
import org.springframework.stereotype.Component;

@Component
public class PixPersistenceMapper {

    public PixDynamoEntity toEntity(Pix domain) {
        if (domain == null) {
            return null;
        }
        PixDynamoEntity entity = new PixDynamoEntity();
        entity.setId(domain.getId());
        entity.setChaveOrigem(domain.getChaveOrigem() != null ? domain.getChaveOrigem().getValor() : null);
        entity.setChaveDestino(domain.getChaveDestino() != null ? domain.getChaveDestino().getValor() : null);
        entity.setValor(domain.getValor());
        entity.setDataTransacao(domain.getDataTransacao());
        entity.setTaxa(domain.getTaxa());
        entity.setPagadorDocumento(domain.getPagador() != null ? domain.getPagador().getDocumento() : null);
        entity.setPagadorNome(domain.getPagador() != null ? domain.getPagador().getNome() : null);
        entity.setRecebedorDocumento(domain.getRecebedor() != null ? domain.getRecebedor().getDocumento() : null);
        entity.setRecebedorNome(domain.getRecebedor() != null ? domain.getRecebedor().getNome() : null);
        entity.setStatus(domain.getStatus() != null ? domain.getStatus() : null);
        entity.setBacenId(domain.getBacenId());
        entity.setMensagemErro(domain.getMensagemErro());
        entity.setTipo(domain.getTipo());
        entity.setEstornadoEm(domain.getEstornadoEm());
        return entity;
    }

    public Pix toDomain(PixDynamoEntity entity) {
        if (entity == null) {
            return null;
        }
        Pix.Builder builder = Pix.builder()
                .id(entity.getId())
                .valor(entity.getValor())
                .dataTransacao(entity.getDataTransacao())
                .taxa(entity.getTaxa())
                .status(entity.getStatus())
                .bacenId(entity.getBacenId())
                .mensagemErro(entity.getMensagemErro())
                .tipo(entity.getTipo())
                .estornadoEm(entity.getEstornadoEm());

        if (entity.getChaveOrigem() != null) {
            builder.chaveOrigem(new ChavePix(entity.getChaveOrigem(), TipoChave.ALEATORIA)); // Assumindo tipo aleatório
        }
        if (entity.getChaveDestino() != null) {
            builder.chaveDestino(new ChavePix(entity.getChaveDestino(), TipoChave.ALEATORIA)); // Assumindo tipo aleatório
        }
        if (entity.getPagadorDocumento() != null) {
            builder.pagador(new Participante(entity.getPagadorNome(), entity.getPagadorDocumento(), null, null, null));
        }
        if (entity.getRecebedorDocumento() != null) {
            builder.recebedor(new Participante(entity.getRecebedorNome(), entity.getRecebedorDocumento(), null, null, null));
        }

        return builder.build();
    }
}


