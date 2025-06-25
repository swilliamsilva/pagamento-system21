package com.pagamento.boleto.infrastructure.adapters.repository;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.infrastructure.adapters.repository.jpa.BoletoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class BoletoRepositoryAdapter implements BoletoRepositoryPort {
	

    private final BoletoJpaRepository boletoJpaRepository;

    public BoletoRepositoryAdapter(BoletoJpaRepository boletoJpaRepository) {
        this.boletoJpaRepository = boletoJpaRepository;
    }

    @Override
    public Boleto salvar(Boleto boleto) {
        return boletoJpaRepository.save(boleto);
    }

    @Override
    public Optional<Boleto> buscarPorId(String id) {
        return boletoJpaRepository.findById(id);
    }

    @Override
    public Boleto atualizar(Boleto boleto) {
        return boletoJpaRepository.save(boleto);
    }

    @Override
    public List<Boleto> buscarTodos() {
        return boletoJpaRepository.findAll();
    }

    @Override
    public void deletarPorId(String id) {
        boletoJpaRepository.deleteById(id);
    }

    @Override
    public List<Boleto> buscarPorStatus(BoletoStatus status) {
        return boletoJpaRepository.findByStatus(status);
    }

    @Override
    public List<Boleto> buscarVencidos(LocalDate dataAtual) {
        return boletoJpaRepository.findByDataVencimentoBeforeAndStatusNot(dataAtual, BoletoStatus.PAGO);
    }
}
