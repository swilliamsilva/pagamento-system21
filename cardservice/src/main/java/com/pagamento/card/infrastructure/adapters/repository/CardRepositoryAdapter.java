/* ========================================================
# Classe: CardRepositoryAdapter
# Módulo: card-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Implementa o acesso a dados usando Cassandra.
#            Refatorado para usar injeção por construtor.
# ======================================================== */

package com.pagamento.card.infrastructure.adapters.repository;

import com.pagamento.card.domain.ports.CardRepositoryPort;
import com.pagamento.card.model.Card;
import com.pagamento.card.repository.cassandra.CardCassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CardRepositoryAdapter implements CardRepositoryPort {

    private final CardCassandraRepository cassandraRepository;

    /**
     * Construtor para injeção de dependência
     * 
     * @param cassandraRepository Repositório Cassandra injetado
     * 
     * Benefícios:
     * 1. Imutabilidade: Campo final garante segurança em threads
     * 2. Testabilidade: Facilita a injeção de mocks em testes
     * 3. Inicialização segura: Elimina NPEs durante inicialização
     */
    public CardRepositoryAdapter(CardCassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

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
    
    // Opcional: Adicionar método toString() para melhor logging
    @Override
    public String toString() {
        return "CardRepositoryAdapter{" +
               "cassandraRepository=" + cassandraRepository.getClass().getSimpleName() +
               '}';
    }
}