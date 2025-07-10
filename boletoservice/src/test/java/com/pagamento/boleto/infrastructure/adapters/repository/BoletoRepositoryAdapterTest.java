package com.pagamento.boleto.infrastructure.adapters.repository;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.infrastructure.adapters.entity.BoletoEntity;
import com.pagamento.boleto.infrastructure.adapters.mapper.BoletoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoletoRepositoryAdapterTest {

    @Mock
    private SpringBoletoRepository springBoletoRepository;

    @Mock
    private BoletoMapper boletoMapper;

    @InjectMocks
    private BoletoRepositoryAdapter boletoRepositoryAdapter;

    private Boleto boleto;
    private BoletoEntity boletoEntity;
    private UUID boletoId;

    @BeforeEach
    void setUp() {
        boletoId = UUID.randomUUID();
        
        boleto = new Boleto();
        boleto.setId(boletoId.toString());
        boleto.setPagador("Cliente A");
        boleto.setBeneficiario("Beneficiário B");
        boleto.setValor(new BigDecimal("1000.00"));
        boleto.setDataVencimento(LocalDate.now().plusDays(30));
        boleto.setStatus(BoletoStatus.EMITIDO);
        
        boletoEntity = new BoletoEntity();
        boletoEntity.setId(boletoId);
        boletoEntity.setPagador("Cliente A");
        boletoEntity.setBeneficiario("Beneficiário B");
        boletoEntity.setValor(new BigDecimal("1000.00"));
        boletoEntity.setDataVencimento(LocalDate.now().plusDays(30));
        boletoEntity.setStatus(BoletoStatus.EMITIDO);
    }

    @Test
    void save_ShouldSaveBoletoSuccessfully() {
        // Arrange
        when(boletoMapper.toEntity(any(Boleto.class))).thenReturn(boletoEntity);
        when(springBoletoRepository.save(any(BoletoEntity.class))).thenReturn(boletoEntity);
        when(boletoMapper.toDomain(any(BoletoEntity.class))).thenReturn(boleto);

        // Act
        Boleto savedBoleto = boletoRepositoryAdapter.save(boleto);

        // Assert
        assertNotNull(savedBoleto);
        assertEquals(boletoId.toString(), savedBoleto.getId());
        verify(springBoletoRepository, times(1)).save(boletoEntity);
        verify(boletoMapper, times(1)).toEntity(boleto);
        verify(boletoMapper, times(1)).toDomain(boletoEntity);
    }

    @Test
    void findById_ShouldReturnBoletoWhenExists() {
        // Arrange
        when(springBoletoRepository.findById(boletoId)).thenReturn(Optional.of(boletoEntity));
        when(boletoMapper.toDomain(boletoEntity)).thenReturn(boleto);

        // Act
        Optional<Boleto> foundBoleto = boletoRepositoryAdapter.findById(boletoId.toString());

        // Assert
        assertTrue(foundBoleto.isPresent());
        assertEquals(boletoId.toString(), foundBoleto.get().getId());
        verify(springBoletoRepository, times(1)).findById(boletoId);
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotFound() {
        // Arrange
        when(springBoletoRepository.findById(boletoId)).thenReturn(Optional.empty());

        // Act
        Optional<Boleto> foundBoleto = boletoRepositoryAdapter.findById(boletoId.toString());

        // Assert
        assertTrue(foundBoleto.isEmpty());
        verify(springBoletoRepository, times(1)).findById(boletoId);
        verify(boletoMapper, never()).toDomain(any());
    }

    @Test
    void updateStatus_ShouldUpdateSuccessfully() {
        // Arrange
        when(springBoletoRepository.findById(boletoId)).thenReturn(Optional.of(boletoEntity));
        when(springBoletoRepository.save(any(BoletoEntity.class))).thenReturn(boletoEntity);
        when(boletoMapper.toDomain(boletoEntity)).thenReturn(boleto);

        // Act
        boletoRepositoryAdapter.updateStatus(
            boletoId.toString(), 
            BoletoStatus.CANCELADO, 
            "Cancelamento solicitado"
        );

        // Assert
        assertEquals(BoletoStatus.CANCELADO, boletoEntity.getStatus());
        assertEquals("Cancelamento solicitado", boletoEntity.getMotivoCancelamento());
        verify(springBoletoRepository, times(1)).findById(boletoId);
        verify(springBoletoRepository, times(1)).save(boletoEntity);
    }

    @Test
    void updateStatus_ShouldThrowWhenNotFound() {
        // Arrange
        when(springBoletoRepository.findById(boletoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            boletoRepositoryAdapter.updateStatus(
                boletoId.toString(), 
                BoletoStatus.CANCELADO, 
                "Motivo"
            )
        );
        verify(springBoletoRepository, times(1)).findById(boletoId);
        verify(springBoletoRepository, never()).save(any());
    }

    @Test
    void incrementReemissaoCount_ShouldIncrementSuccessfully() {
        // Arrange
        boletoEntity.setNumeroReemissoes(1);
        when(springBoletoRepository.findById(boletoId)).thenReturn(Optional.of(boletoEntity));
        when(springBoletoRepository.save(any(BoletoEntity.class))).thenReturn(boletoEntity);
        when(boletoMapper.toDomain(boletoEntity)).thenReturn(boleto);

        // Act
        boletoRepositoryAdapter.incrementReemissaoCount(boletoId.toString());

        // Assert
        assertEquals(2, boletoEntity.getNumeroReemissoes());
        verify(springBoletoRepository, times(1)).findById(boletoId);
        verify(springBoletoRepository, times(1)).save(boletoEntity);
    }
}