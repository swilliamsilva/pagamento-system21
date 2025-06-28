package com.pagamento.boleto;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;

@ActiveProfiles("test")
public abstract class AbstractBoletoRepositoryTest {
    protected BoletoRepositoryPort repository;
    protected Boleto boletoSalvo;
    
    @BeforeEach
    public void setUpBase() {
        if (repository == null) return;
        
        // Criar e salvar um boleto de teste
        Boleto boleto = new Boleto();
        boleto.setPagador("William Silva");
        boleto.setBeneficiario("Empresa XYZ");
        boleto.setValor(BigDecimal.valueOf(100.50));
        boleto.setDataVencimento(LocalDate.now().plusDays(10));
        boleto.setDataEmissao(LocalDate.now());
        boleto.setDocumento("123.456.789-00");
        boleto.setStatus(BoletoStatus.PENDENTE);
        
        boletoSalvo = repository.save(boleto);
    }
    
    @Test
    void testSave() {
        Boleto novoBoleto = new Boleto();
        novoBoleto.setPagador("Novo Pagador");
        novoBoleto.setBeneficiario("Novo Beneficiário");
        novoBoleto.setValor(BigDecimal.valueOf(200.75));
        novoBoleto.setDataVencimento(LocalDate.now().plusDays(20));
        
        Boleto salvo = repository.save(novoBoleto);
        
        assertNotNull(salvo.getId());
        assertEquals(novoBoleto.getPagador(), salvo.getPagador());
        assertEquals(novoBoleto.getBeneficiario(), salvo.getBeneficiario());
    }

    @Test
    void testFindById() {
        Optional<Boleto> encontrado = repository.findById(boletoSalvo.getId());
        
        assertTrue(encontrado.isPresent());
        assertEquals(boletoSalvo.getId(), encontrado.get().getId());
        assertEquals("William Silva", encontrado.get().getPagador());
    }

    @Test
    void testFindAll() {
        List<Boleto> boletos = repository.findAll();
        assertThat(boletos).isNotEmpty();
        assertThat(boletos.get(0).getBeneficiario()).isEqualTo("Empresa XYZ");
    }

    @Test
    void testDeleteById() {
        repository.deleteById(boletoSalvo.getId());
        Optional<Boleto> deletado = repository.findById(boletoSalvo.getId());
        assertFalse(deletado.isPresent());
    }

    @Test
    void testUpdate() {
        boletoSalvo.setValor(BigDecimal.valueOf(150.99));
        Boleto atualizado = repository.save(boletoSalvo);
        assertEquals(BigDecimal.valueOf(150.99), atualizado.getValor());
    }

    @Test
    void testFindByDataVencimentoBetween() {
        LocalDate inicio = LocalDate.now().minusDays(1);
        LocalDate fim = LocalDate.now().plusDays(30);
        
        List<Boleto> boletos = repository.findByDataVencimentoBetween(inicio, fim);
        assertThat(boletos).isNotEmpty();
        assertThat(boletos.get(0).getId()).isEqualTo(boletoSalvo.getId());
    }

    @Test
    void testFindByStatus() {
        List<Boleto> pendentes = repository.findByStatus(BoletoStatus.PENDENTE);
        assertThat(pendentes).isNotEmpty();
        assertThat(pendentes.get(0).getStatus()).isEqualTo(BoletoStatus.PENDENTE);
    }

    @Test
    void testSave_DuplicateId() {
        Boleto boletoComMesmoId = new Boleto();
        boletoComMesmoId.setId(boletoSalvo.getId());
        boletoComMesmoId.setPagador("Outro Pagador");
        
        assertThrows(OptimisticLockingFailureException.class, () -> repository.save(boletoComMesmoId));
    }

    @Test
    void testFindByDocumentoPagador() {
        List<Boleto> boletos = repository.findByDocumentoPagador("123.456.789-00");
        assertThat(boletos).isNotEmpty();
        assertThat(boletos.get(0).getDocumento()).isEqualTo("123.456.789-00");
    }
    
    @Test
    abstract void deveSalvarEBuscarBoleto();
}