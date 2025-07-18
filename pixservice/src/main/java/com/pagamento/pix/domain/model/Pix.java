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
    private LocalDateTime estornadoEm;

    // Construtor privado para uso do Builder
    private Pix() {
        registrarEstado(PixStatus.EM_PROCESSAMENTO, "Criação da transação");
    }

    // Getters
    public String getId() { return id; }
    public ChavePix getChaveOrigem() { return chaveOrigem; }
    public ChavePix getChaveDestino() { return chaveDestino; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getDataTransacao() { return dataTransacao; }
    public double getTaxa() { return taxa; }
    public Participante getPagador() { return pagador; }
    public Participante getRecebedor() { return recebedor; }
    public PixStatus getStatus() { return status; }
    public String getBacenId() { return bacenId; }
    public String getMensagemErro() { return mensagemErro; }
    public String getTipo() { return tipo; }
    public LocalDateTime getEstornadoEm() { return estornadoEm; }
    
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
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("Mensagem de erro obrigatória");
        }
        registrarEstado(PixStatus.ERRO, mensagem);
        this.mensagemErro = mensagem;
    }

    public void iniciarEstorno(String motivo) {
        if (!permiteEstorno()) {
            throw new IllegalStateException("Estorno não permitido para status: " + status);
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo do estorno obrigatório");
        }
        registrarEstado(PixStatus.ESTORNANDO, "Iniciando estorno: " + motivo);
        this.mensagemErro = null;
    } 
    
    public void confirmarEstorno() {
        if (status != PixStatus.ESTORNANDO) {
            throw new IllegalStateException("Estorno não iniciado");
        }
        this.estornadoEm = LocalDateTime.now();
        registrarEstado(PixStatus.ESTORNADO, "Estorno confirmado");
    }
    
    public void falharEstorno(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("Mensagem de erro obrigatória");
        }
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
        
        // Método from() para clonagem de instâncias
        public Builder from(Pix source) {
            if (source != null) {
                this.pix.id = source.id;
                this.pix.chaveOrigem = source.chaveOrigem;
                this.pix.chaveDestino = source.chaveDestino;
                this.pix.valor = source.valor;
                this.pix.dataTransacao = source.dataTransacao;
                this.pix.taxa = source.taxa;
                this.pix.pagador = source.pagador;
                this.pix.recebedor = source.recebedor;
                this.pix.status = source.status;
                this.pix.bacenId = source.bacenId;
                this.pix.mensagemErro = source.mensagemErro;
                this.pix.tipo = source.tipo;
                // Não copiamos o histórico de estados e estornadoEm pois são específicos de cada instância
            }
            return this;
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
            if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor deve ser positivo");
            }
            pix.valor = valor;
            return this;
        }

        public Builder dataTransacao(LocalDateTime dataTransacao) {
            if (dataTransacao == null) {
                throw new IllegalArgumentException("Data/hora obrigatória");
            }
            pix.dataTransacao = dataTransacao;
            return this;
        }

        public Builder taxa(double taxa) {
            if (taxa < 0) {
                throw new IllegalArgumentException("Taxa não pode ser negativa");
            }
            pix.taxa = taxa;
            return this;
        }

        public Builder pagador(Participante pagador) {
            if (pagador == null) {
                throw new IllegalArgumentException("Pagador obrigatório");
            }
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