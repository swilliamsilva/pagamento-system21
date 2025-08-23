package com.pagamento.pix.service;

import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.core.ports.out.PixRepositoryPort;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.service.PixValidator;
import com.pagamento.pix.domain.ports.in.PixServicePort;
import com.pagamento.pix.exception.PixNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class PixService implements PixServicePort {

    private final PixValidator pixValidator;
    private final PixRepositoryPort pixRepositoryPort;
    private final BacenPort bacenPort;

    public PixService(PixValidator pixValidator,
                      PixRepositoryPort pixRepositoryPort,
                      BacenPort bacenPort) {
        this.pixValidator = pixValidator;
        this.pixRepositoryPort = pixRepositoryPort;
        this.bacenPort = bacenPort;
    }

    @Override
    public Pix processarPix(Pix pix) {
        if (!pixValidator.validar(pix)) {
            throw new IllegalArgumentException("Pix inválido");
        }
        // Lógica de processamento existente
        pix.marcarComoProcessado("BACEN_ID_MOCK"); // Exemplo, substituir pela integração real
        return pixRepositoryPort.salvar(pix);
    }

    @Override
    public void estornarPix(Pix pix, String motivo) {
        if (!pix.permiteEstorno()) {
            throw new IllegalStateException("Estorno não permitido para status: " + pix.getStatus());
        }

        pix.iniciarEstorno(motivo);
        pixRepositoryPort.salvar(pix);

        try {
            bacenPort.estornarTransacao(pix.getBacenId());
            pix.confirmarEstorno();
        } catch (Exception e) {
            pix.falharEstorno("Erro BACEN: " + e.getMessage());
        }

        pixRepositoryPort.salvar(pix);
    }

    @Override
    public Pix obterPorId(String id) {
        return pixRepositoryPort.obterPorId(id)
                .orElseThrow(() -> new PixNotFoundException("Pix não encontrado com ID: " + id));
    }

    @Override
    public Page<Pix> pesquisar(
        String documentoPagador,
        String chaveDestino,
        String status,
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    ) {
        LocalDateTime startDateTime = dataInicio != null ?
            dataInicio.atStartOfDay() : null;

        LocalDateTime endDateTime = dataFim != null ?
            dataFim.atTime(LocalTime.MAX) : null;

        return pixRepositoryPort.pesquisar(
            documentoPagador,
            chaveDestino,
            status,
            startDateTime,
            endDateTime,
            pageable
        );
    }
}


