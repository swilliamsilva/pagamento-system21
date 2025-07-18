package com.pagamento.card.infrastructure.adapters.repository;

import com.pagamento.card.model.Card;
import com.pagamento.card.repository.cassandra.CardCassandraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CardRepositoryAdapterTest {

    @Mock
    private CardCassandraRepository cassandraRepository;

    @InjectMocks
    private CardRepositoryAdapter cardRepositoryAdapter;

    private Card testCard;
    private String cardId;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID().toString();
        testCard = Card.builder()
                .id(cardId)
                .cardNumber("4111111111111111")
                .holderName("John Doe")
                .expirationDate("12/2025")
                .cvv("123")
                .build();
    }

    @Test
    @DisplayName("Deve encontrar cartão por ID quando existir")
    void findById_shouldReturnCard_whenExists() {
        // Given
        given(cassandraRepository.findById(cardId)).willReturn(Optional.of(testCard));

        // When
        Optional<Card> result = cardRepositoryAdapter.findById(cardId);

        // Then
        assertThat(result)
                .isPresent()
                .containsSame(testCard);
        
        then(cassandraRepository).should(times(1)).findById(cardId);
    }

    @Test
    @DisplayName("Deve retornar vazio quando cartão não existir")
    void findById_shouldReturnEmpty_whenNotExists() {
        // Given
        given(cassandraRepository.findById(anyString())).willReturn(Optional.empty());

        // When
        Optional<Card> result = cardRepositoryAdapter.findById("non-existent-id");

        // Then
        assertThat(result).isEmpty();
        then(cassandraRepository).should(times(1)).findById("non-existent-id");
    }

    @Test
    @DisplayName("Deve salvar cartão com sucesso")
    void save_shouldPersistCard() {
        // Given
        given(cassandraRepository.save(testCard)).willReturn(testCard);

        // When
        Card savedCard = cardRepositoryAdapter.save(testCard);

        // Then
        assertThat(savedCard).isSameAs(testCard);
        then(cassandraRepository).should(times(1)).save(testCard);
    }

    @Test
    @DisplayName("Deve salvar novo cartão com ID gerado")
    void save_shouldGenerateId_whenNewCard() {
        // Given
        Card newCard = Card.builder()
                .cardNumber("5555555555554444")
                .holderName("Jane Smith")
                .expirationDate("06/2026")
                .cvv("456")
                .build();
        
        given(cassandraRepository.save(newCard)).willAnswer(invocation -> {
            Card toSave = invocation.getArgument(0);
            return toSave.toBuilder().id(UUID.randomUUID().toString()).build();
        });

        // When
        Card savedCard = cardRepositoryAdapter.save(newCard);

        // Then
        assertThat(savedCard)
                .isNotNull()
                .satisfies(card -> {
                    assertThat(card.getId()).isNotBlank();
                    assertThat(card.getCardNumber()).isEqualTo(newCard.getCardNumber());
                });
        
        then(cassandraRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("Deve deletar cartão por ID")
    void deleteById_shouldRemoveCard() {
        // Given
        // Nenhuma preparação especial necessária para delete

        // When
        cardRepositoryAdapter.deleteById(cardId);

        // Then
        then(cassandraRepository).should(times(1)).deleteById(cardId);
        then(cassandraRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("Deve lidar com deleção de ID inexistente")
    void deleteById_shouldHandleNonExistentId() {
        // Given
        // Não é necessário preparar comportamento para ID inexistente

        // When
        cardRepositoryAdapter.deleteById("non-existent-id");

        // Then
        then(cassandraRepository).should(times(1)).deleteById("non-existent-id");
    }

    @Test
    @DisplayName("Não deve fazer nada quando deletar com ID nulo")
    void deleteById_shouldDoNothing_whenNullId() {
        // When
        cardRepositoryAdapter.deleteById(null);

        // Then
        then(cassandraRepository).should(never()).deleteById(any());
    }
}