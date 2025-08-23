package com.pagamento.pix.infrastructure.adapters.repository;

import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.ports.PixRepositoryPort;
import com.pagamento.pix.infrastructure.persistence.PixDynamoRepositoryAdapter;
import com.pagamento.pix.infrastructure.persistence.PixPersistenceMapper;
import com.pagamento.pix.repository.dynamo.PixDynamoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PixRepositoryAdapter implements PixRepositoryPort {

    private final PixDynamoRepositoryAdapter dynamoAdapter;
    private final PixPersistenceMapper mapper;

    public PixRepositoryAdapter(PixDynamoRepositoryAdapter dynamoAdapter,
                               PixPersistenceMapper mapper) {
        this.dynamoAdapter = dynamoAdapter;
        this.mapper = mapper;
    }

    @Override
    public Pix salvar(Pix pix) {
        PixDynamoEntity entity = mapper.toEntity(pix);
        PixDynamoEntity savedEntity = dynamoAdapter.salvar(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Pix> obterPorId(String id) {
        return dynamoAdapter.obterPorId(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deletarPorId(String id) {
        dynamoAdapter.deletarPorId(id);
    }

    @Override
    public Page<Pix> pesquisar(
        String documentoPagador,
        String chaveDestino,
        String status,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        Pageable pageable
    ) {
        return dynamoAdapter.pesquisar(
            documentoPagador,
            chaveDestino,
            status,
            dataInicio,
            dataFim,
            pageable
        ).map(mapper::toDomain);
    }
}


