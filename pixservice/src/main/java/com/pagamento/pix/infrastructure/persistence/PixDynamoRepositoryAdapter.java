package com.pagamento.pix.infrastructure.persistence;

import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.repository.dynamo.PixDynamoEntity;
import com.pagamento.pix.repository.dynamo.PixDynamoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Component
public class PixDynamoRepositoryAdapter {

    private final PixDynamoRepository dynamoRepository;

    public PixDynamoRepositoryAdapter(PixDynamoRepository dynamoRepository) {
        this.dynamoRepository = dynamoRepository;
    }

    public PixDynamoEntity salvar(PixDynamoEntity entity) {
        return dynamoRepository.save(entity);
    }

    public Optional<PixDynamoEntity> obterPorId(String id) {
        return dynamoRepository.findById(id);
    }

    public void deletarPorId(String id) {
        dynamoRepository.deleteById(id);
    }

    public Page<PixDynamoEntity> pesquisar(
        String documentoPagador,
        String chaveDestino,
        String status,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        Pageable pageable
    ) {
        // Implementação de pesquisa no DynamoDB
        // Por simplicidade, retornando uma página vazia. A implementação real dependeria
        // de como o DynamoDB é consultado (ex: usando DynamoDBMapper ou DynamoDBQueryExpression)
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
}


