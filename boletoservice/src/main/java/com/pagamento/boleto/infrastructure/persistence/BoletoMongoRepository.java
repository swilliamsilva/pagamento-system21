package com.pagamento.boleto.infrastructure.persistence;

import com.pagamento.boleto.domain.model.BoletoDocument;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoletoMongoRepository extends MongoRepository<BoletoDocument, UUID> {
}