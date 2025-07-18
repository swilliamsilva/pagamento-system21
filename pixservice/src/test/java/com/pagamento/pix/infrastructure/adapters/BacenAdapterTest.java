package com.pagamento.pix.infrastructure.adapters;

import com.pagamento.pix.domain.model.ChavePix;
import com.pagamento.pix.domain.model.Participante;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.service.PixValidator;
import com.pagamento.pix.infrastructure.integration.BacenClient;
import com.pagamento.pix.infrastructure.integration.BacenIntegrationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BacenAdapterTest {

    @Mock
    private BacenClient bacenClient;

    @InjectMocks
    private BacenAdapter adapter;

    private Pix criarPixValido() {
        Participante pagador = new Participante();
        pagador.setNome("João Silva");
        pagador.setDocumento("52998224725"); // CPF válido
        pagador.setIspb("12345678");
        pagador.setAgencia("0001");
        pagador.setConta("12345-6");

        return Pix.builder()
                .id("PIX-123")
                .chaveOrigem(new ChavePix("origem@email.com"))
                .chaveDestino(new ChavePix("destino@email.com"))
                .valor(new BigDecimal("150.99"))
                .pagador(pagador)
                .tipo("EMAIL")
                .build();
    }

    @Test
    void registrarPix_deveExecutar3Tentativas() {
        // Arrange
        Pix pix = criarPixValido();
        PixValidator validator = new PixValidator();
        
        // Garante que o pix é válido para o teste
        assertTrue(validator.validar(pix), "Pix deve ser válido para o teste");
        
        // Configura o mock para lançar exceção
        when(bacenClient.enviarTransacao(any(Pix.class)))
            .thenThrow(BacenIntegrationException.class);

        // Act & Assert
        assertThrows(BacenIntegrationException.class, () -> 
            adapter.enviarTransacao(pix));

        // Verifica que foram feitas 3 tentativas
        verify(bacenClient, times(3)).enviarTransacao(any(Pix.class));
    }
}