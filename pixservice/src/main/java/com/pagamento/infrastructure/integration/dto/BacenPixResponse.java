package com.pagamento.infrastructure.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BacenPixResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("mensagem")
    private String mensagem;

    @JsonProperty("codigoRetorno")
    private String codigoRetorno;

    @JsonProperty("dataHoraProcessamento")
    private String dataHoraProcessamento;

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

    // Getters e Setters
}