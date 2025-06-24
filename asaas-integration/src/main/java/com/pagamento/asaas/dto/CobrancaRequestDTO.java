package com.pagamento.asaas.dto;




import com.fasterxml.jackson.annotation.JsonProperty;

public class CobrancaRequestDTO {
    @JsonProperty("customer")
    private String customerId;
    
    @JsonProperty("billingType")
    private String billingType;
    
    @JsonProperty("value")
    private Double value;
    
    @JsonProperty("dueDate")
    private String dueDate;
    
    // Getters e Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getBillingType() { return billingType; }
    public void setBillingType(String billingType) { this.billingType = billingType; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}