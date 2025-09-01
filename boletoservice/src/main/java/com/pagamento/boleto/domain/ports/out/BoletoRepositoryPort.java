package com.pagamento.boleto.domain.ports.out;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoletoRepositoryPort {

    Boleto salvar(Boleto boleto);

    Boleto atualizar(Boleto boleto);

    Optional<Boleto> buscarPorId(String id);

    Page<Boleto> listarTodos(Pageable pageable);

    List<Boleto> buscarPorStatus(BoletoStatus status);

    List<Boleto> buscarVencidos(LocalDate dataLimite);

    List<Boleto> buscarPorDocumentoPagador(String documento);

    List<Boleto> buscarPorVencimentoEntre(LocalDate inicio, LocalDate fim);

    void deletar(UUID id);

    void updateStatus(UUID id, BoletoStatus status, String motivoCancelamento);

    void incrementReemissaoCount(UUID id);
}


