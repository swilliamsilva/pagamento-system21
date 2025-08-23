package com.pagamento.pix.repository.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.pagamento.pix.domain.model.PixStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@DynamoDBTable(tableName = "PixTransactions")
public class PixDynamoEntity {

    private String id;
    private String chaveOrigem;
    private String chaveDestino;
    private BigDecimal valor;
    private LocalDateTime dataTransacao;
    private double taxa;
    private String pagadorDocumento;
    private String pagadorNome;
    private String recebedorDocumento;
    private String recebedorNome;
    private PixStatus status;
    private String bacenId;
    private String mensagemErro;
    private String tipo;
    private LocalDateTime estornadoEm;

    @DynamoDBHashKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute
    public String getChaveOrigem() {
        return chaveOrigem;
    }

    public void setChaveOrigem(String chaveOrigem) {
        this.chaveOrigem = chaveOrigem;
    }

    @DynamoDBAttribute
    public String getChaveDestino() {
        return chaveDestino;
    }

    public void setChaveDestino(String chaveDestino) {
        this.chaveDestino = chaveDestino;
    }

    @DynamoDBAttribute
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @DynamoDBAttribute
    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    @DynamoDBAttribute
    public double getTaxa() {
        return taxa;
    }

    public void setTaxa(double taxa) {
        this.taxa = taxa;
    }

    @DynamoDBAttribute
    public String getPagadorDocumento() {
        return pagadorDocumento;
    }

    public void setPagadorDocumento(String pagadorDocumento) {
        this.pagadorDocumento = pagadorDocumento;
    }

    @DynamoDBAttribute
    public String getPagadorNome() {
        return pagadorNome;
    }

    public void setPagadorNome(String pagadorNome) {
        this.pagadorNome = pagadorNome;
    }

    @DynamoDBAttribute
    public String getRecebedorDocumento() {
        return recebedorDocumento;
    }

    public void setRecebedorDocumento(String recebedorDocumento) {
        this.recebedorDocumento = recebedorDocumento;
    }

    public void setRecebedorNome(String recebedorNome) {
        this.recebedorNome = recebedorNome;
    }

    @DynamoDBAttribute
    public String getRecebedorNome() {
        return recebedorNome;
    }

    @DynamoDBTypeConvertedEnum
    public PixStatus getStatus() {
        return status;
    }

    public void setStatus(PixStatus status) {
        this.status = status;
    }

    @DynamoDBAttribute
    public String getBacenId() {
        return bacenId;
    }

    public void setBacenId(String bacenId) {
        this.bacenId = bacenId;
    }

    @DynamoDBAttribute
    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    @DynamoDBAttribute
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @DynamoDBAttribute
    public LocalDateTime getEstornadoEm() {
        return estornadoEm;
    }

    public void setEstornadoEm(LocalDateTime estornadoEm) {
        this.estornadoEm = estornadoEm;
    }
}


