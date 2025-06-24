/* ========================================================
# Classe: Pix
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Entidade que representa uma transação Pix armazenada no DynamoDB.
# ======================================================== */

package com.pagamento.pix.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@DynamoDBTable(tableName = "pix")
public class Pix {

    private String id;
    private String chaveDestino; // Alterado para String
    private String tipo;
    private BigDecimal valor;
    private LocalDateTime dataTransacao;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @DynamoDBAttribute(attributeName = "chaveDestino")
    public String getChaveDestino() { // Retorno compatível
        return chaveDestino;
    }

    public void setChaveDestino(String chaveDestino) { // Parâmetro corrigido
        this.chaveDestino = chaveDestino;
    }
  
    @DynamoDBAttribute(attributeName = "tipo")
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @DynamoDBTypeConverted(converter = BigDecimalConverter.class)
    @DynamoDBAttribute(attributeName = "valor")
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "dataTransacao")
    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    // Conversor customizado para BigDecimal no DynamoDB
    public static class BigDecimalConverter implements DynamoDBTypeConverter<String, BigDecimal> {
        @Override
        public String convert(BigDecimal object) {
            return object != null ? object.toString() : null;
        }

        @Override
        public BigDecimal unconvert(String object) {
            return object != null ? new BigDecimal(object) : null;
        }
    }

    // Conversor customizado para LocalDateTime no DynamoDB
    public static class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {
        @Override
        public String convert(LocalDateTime object) {
            return object != null ? object.toString() : null;
        }

        @Override
        public LocalDateTime unconvert(String object) {
            return object != null ? LocalDateTime.parse(object) : null;
        }
    }

	public void setChaveDestino1(Object chaveDestino2) {
		// TODO Auto-generated method stub
		
	}

	public void setChaveDestino11(Object chaveDestino2) {
		// TODO Auto-generated method stub
		
	}
}
