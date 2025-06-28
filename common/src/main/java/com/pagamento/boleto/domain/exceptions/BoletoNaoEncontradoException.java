package com.pagamento.boleto.domain.exceptions;

import java.util.Map;

import com.pagamento.common.resilience.BusinessExceptionBase;

public class BoletoNaoEncontradoException extends BusinessExceptionBase {
    
    public BoletoNaoEncontradoException(String id) {
        super("Boleto não encontrado: " + id, 
              "BOLETO_NAO_ENCONTRADO",
              Map.of("boletoId", id));
    }
}