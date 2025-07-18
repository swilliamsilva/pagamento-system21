package com.pagamento.pix.core.ports.out;

import com.pagamento.pix.domain.model.Pix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PixRepositoryPort {
    Pix salvar(Pix pix);
    Pix obterPorId(String id);
    
    Page<Pix> pesquisar(
        String documentoPagador,
        String chaveDestino,
        String status,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        Pageable pageable
    );
}