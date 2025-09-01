package com.pagamento.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BoletoRequestDTO {
    @NotBlank(message = "Pagador é obrigatório")
    private String pagador;
    
    @NotBlank(message = "Beneficiário é obrigatório")
    private String beneficiario;
    
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;
    
    @NotNull(message = "Data de vencimento é obrigatória")
    @Future(message = "Data de vencimento deve ser futura")
    private LocalDate dataVencimento;
    
    private String documento;
    private String instrucoes;
    private String localPagamento;

    private BoletoRequestDTO(Builder builder) {
        this.pagador = builder.pagador;
        this.beneficiario = builder.beneficiario;
        this.valor = builder.valor;
        this.dataVencimento = builder.dataVencimento;
        this.documento = builder.documento;
        this.instrucoes = builder.instrucoes;
        this.localPagamento = builder.localPagamento;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPagador() {
        return pagador;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public String getDocumento() {
        return documento;
    }

    public String getInstrucoes() {
        return instrucoes;
    }

    public String getLocalPagamento() {
        return localPagamento;
    }

    public static class Builder {
        private String pagador;
        private String beneficiario;
        private BigDecimal valor;
        private LocalDate dataVencimento;
        private String documento;
        private String instrucoes;
        private String localPagamento;

        public Builder pagador(String pagador) {
            this.pagador = pagador;
            return this;
        }

        public Builder beneficiario(String beneficiario) {
            this.beneficiario = beneficiario;
            return this;
        }

        public Builder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public Builder dataVencimento(LocalDate dataVencimento) {
            this.dataVencimento = dataVencimento;
            return this;
        }

        public Builder documento(String documento) {
            this.documento = documento;
            return this;
        }

        public Builder instrucoes(String instrucoes) {
            this.instrucoes = instrucoes;
            return this;
        }

        public Builder localPagamento(String localPagamento) {
            this.localPagamento = localPagamento;
            return this;
        }

        public BoletoRequestDTO build() {
            return new BoletoRequestDTO(this);
        }
    }
}


