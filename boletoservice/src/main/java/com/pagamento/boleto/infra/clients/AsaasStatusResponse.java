package com.pagamento.boleto.infra.clients;

public class AsaasStatusResponse {
    private String status;
    private String dueDate;
    private String paymentDate;

    public AsaasStatusResponse() {}

    public AsaasStatusResponse(String status, String dueDate) {
        this.status = status;
        this.dueDate = dueDate;
    }

    public AsaasStatusResponse(String status, String dueDate, String paymentDate) {
        this.status = status;
        this.dueDate = dueDate;
        this.paymentDate = paymentDate;
    }

    // Getters e Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}