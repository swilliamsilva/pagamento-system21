package com.pagamento.pix.domain.ports.in;

import com.pagamento.pix.domain.model.Pix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PixServicePort {
    Pix processarPix(Pix pix);
    void estornarPix(Pix pix, String motivo);
    Pix obterPorId(String id);
    Page<Pix> pesquisar(
        String documentoPagador,
        String chaveDestino,
        String status,
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    );
}


