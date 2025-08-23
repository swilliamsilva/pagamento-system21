package com.pagamento.pix.application.service;

import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.model.PixStatus;
import com.pagamento.pix.domain.ports.PixRepositoryPort;
import com.pagamento.pix.domain.ports.in.PixServicePort;
import com.pagamento.pix.domain.service.PixValidator;
import com.pagamento.pix.exception.PixValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class PixServiceImpl implements PixServicePort {

    private final BacenPort bacenPort;
    private final PixRepositoryPort pixRepository;
    private final PixValidator pixValidator;

    public PixServiceImpl(
        BacenPort bacenPort,
        PixRepositoryPort pixRepository,
        PixValidator pixValidator
    ) {
        this.bacenPort = bacenPort;
        this.pixRepository = pixRepository;
        this.pixValidator = pixValidator;
    }

    @Override
    public Pix processarPix(Pix pix) {
        if (!pixValidator.validar(pix)) {
            throw new PixValidationException("Dados do Pix inválidos");
        }
        
        // Orquestração via BacenPort
        bacenPort.enviarPix(pix);
        pix.marcarComoProcessado("BACEN_ID_MOCK"); // Substituir pelo ID real do BACEN
        
        // Persistência
        return pixRepository.salvar(pix);
    }

    @Override
    public void estornarPix(Pix pix, String motivo) {
        if (!pix.permiteEstorno()) {
            throw new IllegalStateException("Estorno não permitido para status: " + pix.getStatus());
        }

        pix.iniciarEstorno(motivo);
        pixRepository.salvar(pix);

        try {
            bacenPort.estornarTransacao(pix.getBacenId());
            pix.confirmarEstorno();
        } catch (Exception e) {
            pix.falharEstorno("Erro BACEN: " + e.getMessage());
        }

        pixRepository.salvar(pix);
    }

    @Override
    public Pix obterPorId(String id) {
        return pixRepository.obterPorId(id).orElse(null);
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

        return pixRepository.pesquisar(
            documentoPagador,
            chaveDestino,
            status,
            startDateTime,
            endDateTime,
            pageable
        );
    }
}


