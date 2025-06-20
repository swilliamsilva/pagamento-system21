/* ========================================================
# Classe: BoletoMapper
# Módulo: boleto-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Conversão entre DTOs e entidades de domínio.
# ======================================================== */

package com.pagamento.boleto.application;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.model.Boleto;

public class BoletoMapper {

    public static Boleto toDomain(BoletoRequestDTO dto) {
        // TODO: Mapear para entidade de domínio
        return new Boleto(null, dto.valor(), dto.descricao(), dto.vencimento());
    }
}
