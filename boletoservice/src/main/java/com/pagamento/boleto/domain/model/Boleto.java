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

import com.pagamento.boleto.application.mapper.DadosTecnicos;

@Entity
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @ElementCollection
    @CollectionTable(name = "boleto_historico_status", joinColumns = @JoinColumn(name = "boleto_id"))
    private List<BoletoStatusHistorico> historicoStatus = new ArrayList<>();

    private String documento;
    private String instrucoes;
    private String localPagamento;

    @Embedded
    private DadosTecnicosBoleto dadosTecnicos;

    private String idExterno;
    private String boletoOriginalId;
    private int numeroReemissoes;
    private String motivoCancelamento;
    private LocalDateTime dataUltimaAtualizacao;

    // Construtor protegido para JPA
    public Boleto() {
        // Construtor vazio obrigatório pelo JPA
    }
    
    @PrePersist
    @PreUpdate
    private void atualizarData() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    // =============== MÉTODOS DE NEGÓCIO ===============

    /**
     * Marca o boleto como PAGO e registra a data de pagamento.
     * @param dataPagamento Data efetiva do pagamento (não pode ser nula)
     */
    public void marcarComoPago(LocalDate dataPagamento) {
        if (dataPagamento == null) {
            throw new IllegalArgumentException("Data de pagamento não pode ser nula");
        }
        if (this.dataEmissao != null && dataPagamento.isBefore(this.dataEmissao)) {
            throw new IllegalArgumentException("Data de pagamento não pode ser anterior à emissão");
        }
        
        this.dataPagamento = dataPagamento;
        this.setStatus(BoletoStatus.PAGO);
    }

    /**
     * Cancela o boleto com um motivo específico.
     * @param motivo Motivo do cancelamento (não pode ser vazio)
     */
    public void cancelar(String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo de cancelamento é obrigatório");
        }
        
        this.motivoCancelamento = motivo;
        this.setStatus(BoletoStatus.CANCELADO);
    }

    /**
     * Verifica se o boleto está vencido.
     * @return true se a data atual for posterior ao vencimento
     */
    public boolean isVencido() {
        return this.dataVencimento != null 
               && LocalDate.now().isAfter(this.dataVencimento);
    }

    /**
     * Incrementa o contador de reemissoes e atualiza a data de modificação.
     */
    public void incrementarReemissoes() {
        this.numeroReemissoes++;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    /**
     * Adiciona um novo status ao histórico de status
     * @param status Novo status a ser registrado
     */
    public void adicionarStatus(BoletoStatus status) {
        this.setStatus(status);
    }

    // =============== GETTERS/SETTERS ===============

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPagador() {
        return pagador;
    }

    public void setPagador(String pagador) {
        this.pagador = pagador;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BoletoStatus getStatus() {
        return status;
    }

    public void setStatus(BoletoStatus status) {
        // Atualiza histórico apenas se o status mudar
        if (this.status != status) {
            if (this.status != null) {
                this.historicoStatus.add(
                    new BoletoStatusHistorico(this.status, LocalDateTime.now())
                );
            }
            this.status = status;
            this.dataUltimaAtualizacao = LocalDateTime.now();
        }
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(String instrucoes) {
        this.instrucoes = instrucoes;
    }

    public String getLocalPagamento() {
        return localPagamento;
    }

    public void setLocalPagamento(String localPagamento) {
        this.localPagamento = localPagamento;
    }

    public DadosTecnicosBoleto getDadosTecnicos() {
        return dadosTecnicos;
    }

    public void setDadosTecnicos(DadosTecnicosBoleto dadosTecnicos) {
        this.dadosTecnicos = dadosTecnicos;
    }

    public String getIdExterno() {
        return idExterno;
    }

    public void setIdExterno(String idExterno) {
        this.idExterno = idExterno;
    }

    public String getBoletoOriginalId() {
        return boletoOriginalId;
    }

    public void setBoletoOriginalId(String boletoOriginalId) {
        this.boletoOriginalId = boletoOriginalId;
    }

    public int getNumeroReemissoes() {
        return numeroReemissoes;
    }

    public void setNumeroReemissoes(int numeroReemissoes) {
        this.numeroReemissoes = numeroReemissoes;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    protected void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public List<BoletoStatusHistorico> getHistoricoStatus() {
        return new ArrayList<>(historicoStatus);
    }

    protected void setHistoricoStatus(List<BoletoStatusHistorico> historicoStatus) {
        this.historicoStatus = historicoStatus;
    }

    // =============== BUILDER PATTERN ===============
    
    public static BoletoBuilder builder() {
        return new BoletoBuilder();
    }
    
    public static class BoletoBuilder {
        private UUID id;
        private String pagador;
        private String beneficiario;
        private BigDecimal valor;
        private LocalDate dataEmissao;
        private LocalDate dataVencimento;
        private BoletoStatus status;
        private String documento;
        private String instrucoes;
        private String localPagamento;
        private DadosTecnicosBoleto dadosTecnicos;
        private String idExterno;
        private String boletoOriginalId;
        private int numeroReemissoes;
        
        public BoletoBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public BoletoBuilder pagador(String pagador) {
            this.pagador = pagador;
            return this;
        }
        
        public BoletoBuilder beneficiario(String beneficiario) {
            this.beneficiario = beneficiario;
            return this;
        }
        
        public BoletoBuilder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }
        
        public BoletoBuilder dataEmissao(LocalDate dataEmissao) {
            this.dataEmissao = dataEmissao;
            return this;
        }
        
        public BoletoBuilder dataVencimento(LocalDate dataVencimento) {
            this.dataVencimento = dataVencimento;
            return this;
        }
        
        public BoletoBuilder status(BoletoStatus status) {
            this.status = status;
            return this;
        }
        
        public BoletoBuilder documento(String documento) {
            this.documento = documento;
            return this;
        }
        
        public BoletoBuilder instrucoes(String instrucoes) {
            this.instrucoes = instrucoes;
            return this;
        }
        
        public BoletoBuilder localPagamento(String localPagamento) {
            this.localPagamento = localPagamento;
            return this;
        }
        
        public BoletoBuilder dadosTecnicos(DadosTecnicosBoleto dadosTecnicos) {
            this.dadosTecnicos = dadosTecnicos;
            return this;
        }
        
        public BoletoBuilder idExterno(String idExterno) {
            this.idExterno = idExterno;
            return this;
        }
        
        public BoletoBuilder boletoOriginalId(String boletoOriginalId) {
            this.boletoOriginalId = boletoOriginalId;
            return this;
        }
        
        public BoletoBuilder numeroReemissoes(int numeroReemissoes) {
            this.numeroReemissoes = numeroReemissoes;
            return this;
        }
        
        public Boleto build() {
            Boleto boleto = new Boleto();
            
            // Configuração do ID com tratamento adequado
            if (id != null) {
                boleto.id = id;
            } else {
                boleto.id = UUID.randomUUID();
            }
            
            boleto.pagador = pagador;
            boleto.beneficiario = beneficiario;
            boleto.valor = valor;
            boleto.dataEmissao = (dataEmissao != null) ? dataEmissao : LocalDate.now();
            boleto.dataVencimento = dataVencimento;
            boleto.documento = documento;
            boleto.instrucoes = instrucoes;
            boleto.localPagamento = localPagamento;
            boleto.dadosTecnicos = dadosTecnicos;
            boleto.idExterno = idExterno;
            boleto.boletoOriginalId = boletoOriginalId;
            boleto.numeroReemissoes = numeroReemissoes;
            boleto.dataUltimaAtualizacao = LocalDateTime.now();
            
            // Configura status inicial com histórico
            if (status != null) {
                boleto.setStatus(status);
            } else {
                boleto.setStatus(BoletoStatus.EMITIDO);
            }
            
            return boleto;
        }

		public BoletoBuilder motivoCancelamento(String motivoCancelamento) {
			// TODO Auto-generated method stub
			return null;
		}
    }

	public void setDadosTecnicos(DadosTecnicos tecnicos) {
		// TODO Auto-generated method stub
		
	}
}