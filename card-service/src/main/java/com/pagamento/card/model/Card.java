/* ========================================================
# Classe: Card
# Módulo: card-service - model
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Representa os dados de um cartão de pagamento.
# ======================================================== */

package com.pagamento.card.model;

public class Card {

    private String id;
    private String holderName;
    private String number;
    private String expiration;
    private String cvv;

    public Card() {
        // Construtor padrão
    }

    public Card(String id, String holderName, String number, String expiration, String cvv) {
        this.id = id;
        this.holderName = holderName;
        this.number = number;
        this.expiration = expiration;
        this.cvv = cvv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "Card{" +
               "id='" + id + '\'' +
               ", holderName='" + holderName + '\'' +
               ", number='" + number + '\'' +
               ", expiration='" + expiration + '\'' +
               ", cvv='" + cvv + '\'' +
               '}';
    }
}
