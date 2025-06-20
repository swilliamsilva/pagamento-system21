// src/main/java/com/pagamento/card/repository/cassandra/CardCassandraRepository.java
package com.pagamento.card.repository.cassandra;

import com.pagamento.card.model.Card;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardCassandraRepository extends CassandraRepository<Card, String> {
    // Exemplo: List<Card> findByUserId(UUID userId);
}
// O modelo Card precisa estar anotado com @Table e campos com @PrimaryKey, etc.
// TODO: Implement CardCassandraRepository.java