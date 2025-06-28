/* ========================================================
# Classe: CardRepositoryAdapter
# Módulo: card-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Implementa o acesso a dados usando Cassandra.
# ======================================================== */

package com.pagamento.card.infrastructure.adapters.repository;

import com.pagamento.card.domain.ports.CardRepositoryPort;
import com.pagamento.card.model.Card;
import com.pagamento.card.repository.cassandra.CardCassandraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CardRepositoryAdapter implements CardRepositoryPort {

    @Autowired
    private CardCassandraRepository cassandraRepository;

    @Override
    public Optional<Card> findById(String id) {
        return cassandraRepository.findById(id);
    }

    @Override
    public Card save(Card card) {
        return cassandraRepository.save(card);
    }

    @Override
    public void deleteById(String id) {
        cassandraRepository.deleteById(id);
    }
}
