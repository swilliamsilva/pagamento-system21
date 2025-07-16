package com.pagamento.pix.service;

import com.pagamento.pix.application.dto.EstornoRequestDTO;
import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.core.ports.out.PixRepositoryPort;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.model.PixStatus;
import com.pagamento.pix.domain.service.PixValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class PixService {

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

    public Pix processarPix(Pix pix) {
        // Implementação existente...
    }

    public void estornarPix(Pix pix, String motivo) {
        if (!pix.permiteEstorno()) {
            throw new IllegalStateException("Estorno não permitido para status: " + pix.getStatus());
        }
        
        pix.iniciarEstorno();
        pixRepositoryPort.salvar(pix);
        
        try {
            bacenPort.estornarTransacao(pix.getBacenId());
            pix.confirmarEstorno();
        } catch (Exception e) {
            pix.falharEstorno("Erro BACEN: " + e.getMessage());
        }
        
        pixRepositoryPort.salvar(pix);
    }

    public Pix obterPorId(String id) {
        return pixRepositoryPort.obterPorId(id);
    }

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