/* ========================================================
# Interface: CardCassandraRepository
# Módulo: card-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Repositório Spring Data para persistência de cartões no Cassandra.
# ======================================================== */

package com.pagamento.card.repository.cassandra;

import com.pagamento.card.model.Card;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardCassandraRepository extends CassandraRepository<Card, String> {
    // Pode adicionar métodos customizados se necessário
}
