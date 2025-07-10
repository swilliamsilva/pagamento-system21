package com.pagamento.boleto.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Boleto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String pagador;
    private String beneficiario;

    @Positive(message = "Valor deve ser positivo")
    @NotNull(message = "Valor é obrigatório")
    private BigDecimal valor;

    private LocalDate dataEmissao;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    private BoletoStatus status;

    // Histórico de estados
    @ElementCollection
    @CollectionTable(name = "boleto_historico_status", joinColumns = @JoinColumn(name = "boleto_id"))
    private List<BoletoStatusHistorico> historicoStatus = new ArrayList<>();

    private String documento;
    private String instrucoes;
    private String localPagamento;

    // Dados técnicos agregados
    @Embedded
    private DadosTecnicosBoleto dadosTecnicos;

    private String idExterno;
    private String boletoOriginalId;
    private int reemissoes;
    private String motivoCancelamento;
    private LocalDateTime dataUltimaAtualizacao;

    // Construtor protegido para JPA
    public Boleto() {
        // Construtor vazio obrigatório pelo JPA. 
        // Não deve ser utilizado diretamente - utilize o builder pattern para criação.
    }
    
    @PrePersist
    @PreUpdate
    private void atualizarData() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getPagador() { return pagador; }
    public void setPagador(String pagador) { this.pagador = pagador; }
    
    public String getBeneficiario() { return beneficiario; }
    public void setBeneficiario(String beneficiario) { this.beneficiario = beneficiario; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(@Positive(message = "Valor deve ser positivo") @NotNull(message = "Valor é obrigatório") BigDecimal d) { 
        this.valor = d; 
    }
    
    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }
    
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    
    public BoletoStatus getStatus() { return status; }
    public void setStatus(BoletoStatus status) { 
        if (this.status != null) {
            this.historicoStatus.add(new BoletoStatusHistorico(this.status, LocalDateTime.now()));
        }
        this.status = status;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
    
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    
    public String getInstrucoes() { return instrucoes; }
    public void setInstrucoes(String instrucoes) { this.instrucoes = instrucoes; }
    
    public String getLocalPagamento() { return localPagamento; }
    public void setLocalPagamento(String localPagamento) { this.localPagamento = localPagamento; }
    
    public DadosTecnicosBoleto getDadosTecnicos() { return dadosTecnicos; }
    public void setDadosTecnicos(DadosTecnicosBoleto dadosTecnicos) { 
        this.dadosTecnicos = dadosTecnicos; 
    }
    
    public String getIdExterno() { return idExterno; }
    public void setIdExterno(String idExterno) { this.idExterno = idExterno; }
    
    public String getBoletoOriginalId() { return boletoOriginalId; }
    public void setBoletoOriginalId(String boletoOriginalId) { this.boletoOriginalId = boletoOriginalId; }
    
    public int getReemissoes() { return reemissoes; }
    public void incrementarReemissoes() { 
        this.reemissoes++; 
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
    
    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }
    
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    protected void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { 
        this.dataUltimaAtualizacao = dataUltimaAtualizacao; 
    }
    
    public List<BoletoStatusHistorico> getHistoricoStatus() { return historicoStatus; }
    protected void setHistoricoStatus(List<BoletoStatusHistorico> historicoStatus) { 
        this.historicoStatus = historicoStatus; 
    }
    
    // Métodos de negócio
    public void marcarComoPago(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
        this.setStatus(BoletoStatus.PAGO);
    }
    
    public void cancelar(String motivo) {
        this.motivoCancelamento = motivo;
        this.setStatus(BoletoStatus.CANCELADO);
    }
    
    public boolean isVencido() {
        return LocalDate.now().isAfter(dataVencimento);
    }
    
    public void adicionarStatus(BoletoStatus novoStatus) {
        if (this.status != null) {
            this.historicoStatus.add(new BoletoStatusHistorico(this.status, LocalDateTime.now()));
        }
        this.status = novoStatus;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
    
    // Builder Pattern para construção fluente
    public static BoletoBuilder builder() {
        return new BoletoBuilder();
    }
    
    public static class BoletoBuilder {
        private final Boleto boleto = new Boleto();
        
        public BoletoBuilder id(String id) {
            boleto.id = id;
            return this;
        }
        
        public BoletoBuilder pagador(String pagador) {
            boleto.pagador = pagador;
            return this;
        }
        
        // ... outros campos
        
        public Boleto build() {
            if (boleto.id == null) {
                boleto.id = UUID.randomUUID().toString();
            }
            if (boleto.dataEmissao == null) {
                boleto.dataEmissao = LocalDate.now();
            }
            boleto.dataUltimaAtualizacao = LocalDateTime.now();
            return boleto;
        }
    }

    public void setCodigo(String codigo) {
        this.idExterno = codigo;
    }

	public int getNumeroReemissoes() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setNumeroReemissoes(int i) {
		// TODO Auto-generated method stub
		
	}
}