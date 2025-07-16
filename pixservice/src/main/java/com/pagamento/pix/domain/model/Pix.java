package com.pagamento.pix.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pix {
    private String id;
    private ChavePix chaveOrigem;
    private ChavePix chaveDestino;
    private BigDecimal valor;
    private LocalDateTime dataTransacao;
    private double taxa;
    private Participante pagador;
    private Participante recebedor;
    private PixStatus status;
    private String bacenId;
    private String mensagemErro;
    private String tipo;
    private final List<EstadoPix> historicoEstados = new ArrayList<>();

    // Construtor privado para uso do Builder
    private Pix() {
        registrarEstado(PixStatus.EM_PROCESSAMENTO, "Criação da transação");
    }

    // Getters e Setters
    public String getId() { return id; }
    private void setId(String id) { this.id = id; }
    
    public ChavePix getChaveOrigem() { return chaveOrigem; }
    private void setChaveOrigem(ChavePix chaveOrigem) { this.chaveOrigem = chaveOrigem; }
    
    public ChavePix getChaveDestino() { return chaveDestino; }
    private void setChaveDestino(ChavePix chaveDestino) { this.chaveDestino = chaveDestino; }
    
    public BigDecimal getValor() { return valor; }
    private void setValor(BigDecimal valor) { 
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        this.valor = valor; 
    }
    
    public LocalDateTime getDataTransacao() { return dataTransacao; }
    private void setDataTransacao(LocalDateTime dataTransacao) { 
        if (dataTransacao == null) {
            throw new IllegalArgumentException("Data/hora obrigatória");
        }
        this.dataTransacao = dataTransacao; 
    }
    
    public double getTaxa() { return taxa; }
    private void setTaxa(double taxa) { 
        if (taxa < 0) {
            throw new IllegalArgumentException("Taxa não pode ser negativa");
        }
        this.taxa = taxa; 
    }
    
    public Participante getPagador() { return pagador; }
    private void setPagador(Participante pagador) { 
        if (pagador == null) {
            throw new IllegalArgumentException("Pagador obrigatório");
        }
        this.pagador = pagador; 
    }
    
    public Participante getRecebedor() { return recebedor; }
    private void setRecebedor(Participante recebedor) { this.recebedor = recebedor; }
    
    public PixStatus getStatus() { return status; }
    public void setStatus(PixStatus status) {
        this.status = status;
    }
    
    public String getBacenId() { return bacenId; }
    private void setBacenId(String bacenId) { this.bacenId = bacenId; }
    
    public String getMensagemErro() { return mensagemErro; }
    private void setMensagemErro(String mensagemErro) { this.mensagemErro = mensagemErro; }
    
    public String getTipo() { return tipo; }
    private void setTipo(String tipo) { this.tipo = tipo; }
    
    // Histórico de estados (imutável)
    public List<EstadoPix> getHistoricoEstados() {
        return Collections.unmodifiableList(historicoEstados);
    }

    // Métodos de negócio
    public boolean isProcessado() {
        return status == PixStatus.PROCESSADO;
    }

    public boolean permiteEstorno() {
        return status != null && status.permiteEstorno();
    }

    public void marcarComoProcessado(String bacenId) {
        if (bacenId == null || bacenId.isBlank()) {
            throw new IllegalArgumentException("ID BACEN obrigatório");
        }
        registrarEstado(PixStatus.PROCESSADO, "Transação processada com sucesso");
        this.bacenId = bacenId;
        this.mensagemErro = null;
    }

    public void marcarComoErro(String mensagem) {
        registrarEstado(PixStatus.ERRO, mensagem);
        this.mensagemErro = mensagem;
    }

    public void iniciarEstorno() {
        if (!permiteEstorno()) {
            throw new IllegalStateException("Estorno não permitido para status: " + status);
        }
        registrarEstado(PixStatus.ESTORNANDO, "Iniciando processo de estorno");
        this.mensagemErro = null;
    }

    public void confirmarEstorno() {
        if (status != PixStatus.ESTORNANDO) {
            throw new IllegalStateException("Estorno não iniciado");
        }
        registrarEstado(PixStatus.ESTORNADO, "Estorno confirmado");
        this.mensagemErro = null;
    }

    public void falharEstorno(String mensagem) {
        registrarEstado(PixStatus.ERRO_ESTORNO, mensagem);
        this.mensagemErro = mensagem;
    }
    
    // Registrar transição de estado
    private void registrarEstado(PixStatus novoStatus, String motivo) {
        EstadoPix estado = new EstadoPix(this.status, novoStatus, LocalDateTime.now(), motivo);
        historicoEstados.add(estado);
        this.status = novoStatus;
    }
    
    // Calcula a taxa usando uma estratégia
    public void calcularTaxa(TaxaStrategy estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("Estratégia de taxa obrigatória");
        }
        this.taxa = estrategia.calcular(this.valor);
        registrarEstado(status, "Taxa calculada: " + this.taxa);
    }

    // Método para validação básica
    public boolean isValid() {
        return valor != null && 
               valor.compareTo(BigDecimal.ZERO) > 0 &&
               dataTransacao != null &&
               pagador != null &&
               chaveDestino != null &&
               chaveDestino.validar();
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Pix pix;

        private Builder() {
            this.pix = new Pix();
        }

        public Builder id(String id) {
            pix.id = id;
            return this;
        }

        public Builder chaveOrigem(ChavePix chaveOrigem) {
            pix.chaveOrigem = chaveOrigem;
            return this;
        }

        public Builder chaveDestino(ChavePix chaveDestino) {
            pix.chaveDestino = chaveDestino;
            return this;
        }

        public Builder valor(BigDecimal valor) {
            pix.valor = valor;
            return this;
        }

        public Builder dataTransacao(LocalDateTime dataTransacao) {
            pix.dataTransacao = dataTransacao;
            return this;
        }

        public Builder taxa(double taxa) {
            pix.taxa = taxa;
            return this;
        }

        public Builder pagador(Participante pagador) {
            pix.pagador = pagador;
            return this;
        }

        public Builder recebedor(Participante recebedor) {
            pix.recebedor = recebedor;
            return this;
        }

        public Builder status(PixStatus status) {
            pix.status = status;
            return this;
        }

        public Builder bacenId(String bacenId) {
            pix.bacenId = bacenId;
            return this;
        }

        public Builder mensagemErro(String mensagemErro) {
            pix.mensagemErro = mensagemErro;
            return this;
        }

        public Builder tipo(String tipo) {
            pix.tipo = tipo;
            return this;
        }

        public Pix build() {
            // Validações obrigatórias
            if (pix.valor == null) {
                throw new IllegalStateException("Valor é obrigatório");
            }
            if (pix.pagador == null) {
                throw new IllegalStateException("Pagador é obrigatório");
            }
            if (pix.chaveDestino == null) {
                throw new IllegalStateException("Chave destino é obrigatória");
            }
            if (pix.dataTransacao == null) {
                pix.dataTransacao = LocalDateTime.now();
            }
            return pix;
        }
    }
}