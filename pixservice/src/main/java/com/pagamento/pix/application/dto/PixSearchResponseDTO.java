package com.pagamento.pix.application.dto;

import java.util.List;

public class PixSearchResponseDTO {
    private List<PixResponseDTO> transacoes;
    private int pagina;
    private int tamanhoPagina;
    private long totalElementos;

    public PixSearchResponseDTO(List<PixResponseDTO> transacoes, int pagina, int tamanhoPagina, long totalElementos) {
        this.transacoes = transacoes;
        this.pagina = pagina;
        this.tamanhoPagina = tamanhoPagina;
        this.totalElementos = totalElementos;
    }

    // Getters
    public List<PixResponseDTO> getTransacoes() {
        return transacoes;
    }

    public int getPagina() {
        return pagina;
    }

    public int getTamanhoPagina() {
        return tamanhoPagina;
    }

    public long getTotalElementos() {
        return totalElementos;
    }
}