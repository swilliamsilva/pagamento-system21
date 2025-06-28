/* ========================================================
# Interface: CardRepositoryPort
# Módulo: card-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Porta de saída para persistência de cartões.
# ======================================================== */

package com.pagamento.card.domain.ports;

import com.pagamento.card.model.Card;
import java.util.Optional;

public interface CardRepositoryPort {
    Optional<Card> findById(String id);
    Card save(Card card);
    void deleteById(String id);
}
