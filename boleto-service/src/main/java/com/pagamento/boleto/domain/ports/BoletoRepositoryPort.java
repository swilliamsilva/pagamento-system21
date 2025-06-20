package com.pagamento.boleto.domain.ports;

import com.pagamento.boleto.domain.model.Boleto;

public interface BoletoRepositoryPort {
    void salvar(Boleto boleto);
}
