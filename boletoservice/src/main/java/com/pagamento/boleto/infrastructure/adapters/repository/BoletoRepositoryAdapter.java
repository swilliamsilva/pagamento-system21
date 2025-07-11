package com.pagamento.boleto.infrastructure.adapters.repository;

import com.pagamento.boleto.application.mapper.BoletoMapper;
import com.pagamento.boleto.domain.exception.BoletoNotFoundException;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.infrastructure.persistence.BoletoEntity;
import com.pagamento.boleto.infrastructure.persistence.SpringDataBoletoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BoletoRepositoryAdapter implements BoletoRepositoryPort {

    private final SpringDataBoletoRepository springDataBoletoRepository;
    private final BoletoMapper boletoMapper;

    protected BoletoRepositoryAdapter(SpringDataBoletoRepository springDataBoletoRepository, 
                                     BoletoMapper boletoMapper) {
        this.springDataBoletoRepository = springDataBoletoRepository;
        this.boletoMapper = boletoMapper;
    }
    
    @Override
    public Boleto save(Boleto boleto) {
        BoletoEntity entity = boletoMapper.toEntity(boleto);
        BoletoEntity savedEntity = springDataBoletoRepository.save(entity);
        return boletoMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Boleto> findById(UUID id) {
        return springDataBoletoRepository.findById(id)
                .map(boletoMapper::toDomain);
    }

    @Override
    public void updateStatus(UUID id, BoletoStatus status, String motivoCancelamento) {
        BoletoEntity entity = springDataBoletoRepository.findById(id)
                .orElseThrow(() -> new BoletoNotFoundException("Boleto não encontrado: " + id));
        
        entity.setStatus(status);
        entity.setMotivoCancelamento(motivoCancelamento);
        springDataBoletoRepository.save(entity);
    }

    @Override
    public void incrementReemissaoCount(UUID id) {
        BoletoEntity entity = springDataBoletoRepository.findById(id)
                .orElseThrow(() -> new BoletoNotFoundException("Boleto não encontrado: " + id));
        
        entity.setNumeroReemissoes(entity.getNumeroReemissoes() + 1);
        springDataBoletoRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        springDataBoletoRepository.deleteById(id);
    }

    @Override
    public Boleto salvar(Boleto boleto) {
        return save(boleto);
    }

    @Override
    public Boleto atualizar(Boleto boleto) {
        // Verifica se o boleto existe
        if (!springDataBoletoRepository.existsById(boleto.getId())) {
            throw new BoletoNotFoundException("Boleto não encontrado para atualização: " + boleto.getId());
        }
        return save(boleto);
    }

    @Override
    public Optional<Boleto> buscarPorId(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return findById(uuid);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Boleto> buscarTodos() {
        return springDataBoletoRepository.findAll().stream()
                .map(boletoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Boleto> buscarPorStatus(BoletoStatus status) {
        return springDataBoletoRepository.findByStatus(status).stream()
                .map(boletoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Boleto> buscarVencidos(LocalDate dataLimite) {
        return springDataBoletoRepository.findByDataVencimentoBeforeAndStatusNotIn(
                dataLimite, 
                List.of(BoletoStatus.PAGO, BoletoStatus.CANCELADO)
            ).stream()
            .map(boletoMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Boleto> buscarPorDocumentoPagador(String documento) {
        return springDataBoletoRepository.findByDocumento(documento).stream()
                .map(boletoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Boleto> buscarPorVencimentoEntre(LocalDate inicio, LocalDate fim) {
        return springDataBoletoRepository.findByDataVencimentoBetween(inicio, fim).stream()
                .map(boletoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deletarPorId(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            delete(uuid);
        } catch (IllegalArgumentException e) {
            // Logar ou tratar IDs inválidos
        }
    }

    @Override
    public void deletar(UUID id) {
        delete(id);
    }
}