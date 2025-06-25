// CardServiceTest.java (JUnit)
package test.com.pagamento.card.service;

import com.pagamento.card.model.Card;
import com.pagamento.card.service.CardService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardServiceTest {

    @Test
    void deveProcessarPagamentoComSucesso() {
        CardService service = new CardService();
        Card card = new Card("1", "João Silva", "1234567890123456", "12/29", "123");
        assertTrue(service.processarPagamentoCartao(card));
    }
}