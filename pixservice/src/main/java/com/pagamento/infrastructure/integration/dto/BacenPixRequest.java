package com.pagamento.infrastructure.integration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BacenPixRequest {

    @JsonProperty("endToEndId")
    private String endToEndId;

    @JsonProperty("valor")
    private BigDecimal valor;

    @JsonProperty("pagador")
    private Participante pagador;

    @JsonProperty("recebedor")
    private Participante recebedor;

    @JsonProperty("dataHora")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHora;

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("chave")
    private String chave;

    // Construtor padrão necessário para serialização
    public BacenPixRequest() {
        // Construtor vazio intencional para frameworks de serialização
    }

    // Getters e Setters
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

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
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

    public static class Participante {
        @JsonProperty("cpf")
        private String cpf;
        
        @JsonProperty("nome")
        private String nome;
        
        @JsonProperty("ispb")
        private String ispb;
        
        @JsonProperty("agencia")
        private String agencia;
        
        @JsonProperty("conta")
        private String conta;

        // Construtor padrão necessário para serialização
        public Participante() {
            // Construtor vazio intencional para frameworks de serialização
        }

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
}