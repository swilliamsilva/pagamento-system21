package com.pagamento.boleto.infrastructure.adapters.repository.jpa;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pagamento.boleto.domain.model.*;

public interface BoletoMongoRepository extends MongoRepository<BoletoDocument, UUID> {}
