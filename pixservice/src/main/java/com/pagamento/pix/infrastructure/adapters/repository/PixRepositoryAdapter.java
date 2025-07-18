package com.pagamento.pix.infrastructure.adapters.repository;

import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.repository.dynamo.PixDynamoRepository;
import com.pagamento.pix.service.CryptoService;
import org.springframework.stereotype.Component;

@Component
public class PixRepositoryAdapter {

    private final PixDynamoRepository repository;
    private final CryptoService cryptoService;

    public PixRepositoryAdapter(PixDynamoRepository repository, 
                               CryptoService cryptoService) {
        this.repository = repository;
        this.cryptoService = cryptoService;
    }

    public Pix salvar(Pix pix) {
        Pix pixCriptografado = deepCloneAndEncrypt(pix);
        return repository.salvar(pixCriptografado);
    }

    private Pix deepCloneAndEncrypt(Pix original) {
        // Clonagem profunda usando builder pattern
        Pix.PixBuilder builder = Pix.builder()
            .id(original.getId())
            .chaveOrigem(original.getChaveOrigem())
            .chaveDestino(original.getChaveDestino())
            .valor(original.getValor())
            .dataTransacao(original.getDataTransacao())
            .taxa(original.getTaxa())
            .status(original.getStatus())
            .bacenId(original.getBacenId())
            .mensagemErro(original.getMensagemErro())
            .tipo(original.getTipo());

        // Clonar e criptografar participantes
        if (original.getPagador() != null) {
            builder.pagador(cloneAndEncryptParticipante(original.getPagador()));
        }
        if (original.getRecebedor() != null) {
            builder.recebedor(cloneAndEncryptParticipante(original.getRecebedor()));
        }

        return builder.build();
    }

    private Participante cloneAndEncryptParticipante(Participante original) {
        return new Participante(
            original.getNome(),
            cryptoService.encrypt(original.getDocumento()), // Criptografa documento
            original.getIspb(),
            original.getAgencia(),
            original.getConta()
        );
    }
}