package com.pagamento.pix.infrastructure.persistence;

import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.ports.PixRepositoryPort;
import com.pagamento.pix.repository.dynamo.PixDynamoEntity;
import com.pagamento.pix.repository.dynamo.PixDynamoRepository;
import org.springframework.stereotype.Component;

@Component
public class PixDynamoRepositoryAdapter implements PixRepositoryPort {

    private final PixDynamoRepository dynamoRepository;
    private final PixPersistenceMapper mapper;

    public PixDynamoRepositoryAdapter(PixDynamoRepository dynamoRepository, 
                                     PixPersistenceMapper mapper) {
        this.dynamoRepository = dynamoRepository;
        this.mapper = mapper;
    }

    @Override
    public Pix salvar(Pix pix) {
        PixDynamoEntity entity = mapper.toEntity(pix);
        PixDynamoEntity savedEntity = dynamoRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}