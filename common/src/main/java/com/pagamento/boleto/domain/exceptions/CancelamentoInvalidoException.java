package com.pagamento.boleto.domain.exceptions;

import java.util.Map;

import com.pagamento.common.resilience.BusinessExceptionBase;

public class CancelamentoInvalidoException extends BusinessExceptionBase {
    
    public CancelamentoInvalidoException(String motivo) {
        super("Cancelamento inválido: " + motivo, 
              "CANCELAMENTO_INVALIDO",
              Map.of("motivo", motivo));
    }
}