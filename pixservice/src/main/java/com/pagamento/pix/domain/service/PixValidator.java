package com.pagamento.pix.domain.service;

import com.pagamento.pix.domain.model.Pix;
import java.math.BigDecimal;

public class PixValidator {

    public boolean validar(Pix pix) {
        if (pix == null) return false;
        
        return validarValor(pix.getValor()) &&
               validarChaves(pix) &&
               validarParticipantes(pix);
    }

    private boolean validarValor(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean validarChaves(Pix pix) {
        return pix.getChaveOrigem() != null &&
               pix.getChaveDestino() != null &&
               pix.getChaveOrigem().validar() &&
               pix.getChaveDestino().validar() &&
               !pix.getChaveOrigem().getValor().equals(pix.getChaveDestino().getValor());
    }

    private boolean validarParticipantes(Pix pix) {
        return pix.getPagador() != null &&
               pix.getRecebedor() != null &&
               validarDocumento(pix.getPagador().getDocumento()) &&
               validarDocumento(pix.getRecebedor().getDocumento());
    }

    private boolean validarDocumento(String documento) {
        if (documento == null) return false;
        
        if (documento.length() == 11) {
            return DocumentoValidator.validarCPF(documento);
        } else if (documento.length() == 14) {
            return DocumentoValidator.validarCNPJ(documento);
        }
        return false;
    }
}