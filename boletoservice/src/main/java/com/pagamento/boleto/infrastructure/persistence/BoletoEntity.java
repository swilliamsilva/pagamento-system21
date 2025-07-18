package com.pagamento.boleto.infrastructure.persistence;

import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.model.BoletoStatusHistorico;
import com.pagamento.boleto.domain.model.DadosTecnicosBoleto;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "boletos")
public class BoletoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String pagador;

    @Column(nullable = false, length = 100)
    private String beneficiario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoletoStatus status;

    @ElementCollection
    @CollectionTable(
        name = "boleto_historico_status", 
        joinColumns = @JoinColumn(name = "boleto_id")
    )
    private List<BoletoStatusHistorico> historicoStatus = new ArrayList<>();

    @Column(length = 50)
    private String documento;

    @Column(length = 500)
    private String instrucoes;

    @Column(name = "local_pagamento", length = 200)
    private String localPagamento;

    @Embedded
    private DadosTecnicosBoleto dadosTecnicos;

    @Column(name = "id_externo", unique = true, length = 50)
    private String idExterno;

    @Column(name = "boleto_original_id", length = 36)
    private String boletoOriginalId;

    @Column(name = "numero_reemissoes", nullable = false)
    private int numeroReemissoes = 0;

    @Column(name = "motivo_cancelamento", length = 500)
    private String motivoCancelamento;

    @Column(name = "data_ultima_atualizacao", nullable = false)
    private LocalDateTime dataUltimaAtualizacao;

    // Callbacks JPA
    @PrePersist
    @PreUpdate
    private void atualizarData() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    // Método para adicionar histórico de status
    public void adicionarHistoricoStatus(BoletoStatus status) {
        this.historicoStatus.add(new BoletoStatusHistorico(status, LocalDateTime.now()));
    }

    // Getters e Setters completos
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
                adicionarHistoricoStatus(this.status);
            }
            this.status = status;
        }
    }

    public List<BoletoStatusHistorico> getHistoricoStatus() {
        return new ArrayList<>(historicoStatus);
    }

    public void setHistoricoStatus(List<BoletoStatusHistorico> historicoStatus) {
        this.historicoStatus = historicoStatus;
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

    public void incrementarReemissoes() {
        this.numeroReemissoes++;
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

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }
}