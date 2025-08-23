/* ========================================================
# Interface: PixRepositoryPort
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Porta de saída para persistência de transações Pix.
# ======================================================== */

package com.pagamento.pix.domain.ports;

import com.pagamento.pix.domain.model.Pix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PixRepositoryPort {
    Optional<Pix> obterPorId(String id);
    Pix salvar(Pix pix);
    void deletarPorId(String id);
    Page<Pix> pesquisar(
        String documentoPagador,
        String chaveDestino,
        String status,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        Pageable pageable
    );
}


