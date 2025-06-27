package com.pagamento.boleto.infrastructure.adapters.repository;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.infrastructure.adapters.repository.jpa.BoletoJpaRepository;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class BoletoRepositoryAdapter implements BoletoRepositoryPort {

    private final BoletoJpaRepository boletoJpaRepository;

    public BoletoRepositoryAdapter(BoletoJpaRepository boletoJpaRepository) {
        this.boletoJpaRepository = boletoJpaRepository;
    }

    @Override
    public Boleto save(Boleto boleto) {
        return boletoJpaRepository.save(boleto);
    }

    @Override
    public Optional<Boleto> findById(String id) {
        return boletoJpaRepository.findById(id);
    }

    @Override
    public List<Boleto> findAll() {
        return boletoJpaRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        boletoJpaRepository.deleteById(id);
    }

    @Override
    public List<Boleto> findByStatus(BoletoStatus status) {
        return boletoJpaRepository.findByStatus(status);
    }

    @Override
    public List<Boleto> findByDataVencimentoBetween(LocalDate start, LocalDate end) {
        return boletoJpaRepository.findByDataVencimentoBetween(start, end);
    }

    @Override
    public List<Boleto> findByDocumentoPagador(String documento) {
        return boletoJpaRepository.findByDocumento(documento);
    }

    @Override
    public Boleto salvar(Boleto boleto) {
        return save(boleto);
    }

    @Override
    public Optional<Boleto> buscarPorId(String id) {
        return findById(id);
    }

    @Override
    public Boleto atualizar(Boleto boleto) {
        return save(boleto);
    }

    @Override
    public List<Boleto> buscarTodos() {
        return findAll();
    }

    @Override
    public void deletarPorId(String id) {
        deleteById(id);
    }

    @Override
    public List<Boleto> buscarPorStatus(BoletoStatus status) {
        return findByStatus(status);
    }

    @Override
    public List<Boleto> buscarVencidos(LocalDate dataAtual) {
        return boletoJpaRepository.findByDataVencimentoBeforeAndStatusNot(dataAtual, BoletoStatus.PAGO);
    }
}
