package com.pagamento.pix.infrastructure.integration.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BacenPixRequest {

    private String endToEndId;
    private BigDecimal valor;
    private String tipo;
    private String chave;
    private LocalDateTime dataHora;
    private Participante pagador;
    private Participante recebedor;

    // Classe interna estática para Participante
    public static class Participante {
        private String cpf;
        private String nome;
        private String ispb;
        private String agencia;
        private String conta;

        // Getters e Setters
        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getIspb() {
            return ispb;
        }

        public void setIspb(String ispb) {
            this.ispb = ispb;
        }

        public String getAgencia() {
            return agencia;
        }

        public void setAgencia(String agencia) {
            this.agencia = agencia;
        }

        public String getConta() {
            return conta;
        }

        public void setConta(String conta) {
            this.conta = conta;
        }
    }

    // Getters e Setters para BacenPixRequest
    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Participante getPagador() {
        return pagador;
    }

    public void setPagador(Participante pagador) {
        this.pagador = pagador;
    }

    public Participante getRecebedor() {
        return recebedor;
    }

    public void setRecebedor(Participante recebedor) {
        this.recebedor = recebedor;
    }
}