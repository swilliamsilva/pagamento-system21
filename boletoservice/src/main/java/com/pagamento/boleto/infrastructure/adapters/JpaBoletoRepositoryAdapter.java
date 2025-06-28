package com.pagamento.boleto.infrastructure.adapters;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.infrastructure.persistence.SpringDataBoletoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaBoletoRepositoryAdapter implements BoletoRepositoryPort {
    
    private final SpringDataBoletoRepository springRepository;

    @Override
    public Boleto salvar(Boleto boleto) {
        return springRepository.save(boleto);
    }

    @Override
    public Boleto atualizar(Boleto boleto) {
        return springRepository.save(boleto);
    }

    @Override
    public Optional<Boleto> buscarPorId(String id) {
        return springRepository.findById(id);
    }

    @Override
    public List<Boleto> buscarTodos() {
        return springRepository.findAll();
    }

    @Override
    public List<Boleto> buscarPorStatus(BoletoStatus status) {
        return springRepository.findByStatus(status);
    }

    @Override
    public List<Boleto> buscarVencidos(LocalDate dataLimite) {
        return springRepository.findByDataVencimentoBefore(dataLimite);
    }

    @Override
    public List<Boleto> buscarPorDocumentoPagador(String documento) {
        return springRepository.findByDocumento(documento);
    }

    @Override
    public List<Boleto> buscarPorVencimentoEntre(LocalDate inicio, LocalDate fim) {
        return springRepository.findByDataVencimentoBetween(inicio, fim);
    }

    @Override
    public void deletarPorId(String id) {
        springRepository.deleteById(id);
    }
}