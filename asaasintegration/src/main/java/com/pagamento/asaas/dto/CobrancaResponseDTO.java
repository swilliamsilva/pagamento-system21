package com.pagamento.asaas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CobrancaResponseDTO {
    private String id;
    private String status;
    @JsonProperty("invoiceUrl")
    private String invoiceUrl;
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getInvoiceUrl() { return invoiceUrl; }
    public void setInvoiceUrl(String invoiceUrl) { this.invoiceUrl = invoiceUrl; }
}