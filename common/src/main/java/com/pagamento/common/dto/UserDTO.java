package com.pagamento.common.dto;

import com.pagamento.common.validation.ValidCPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDTO {

    @NotBlank(message = "ID não pode ser vazio")
    private String id;

    @NotBlank(message = "Nome não pode ser vazio")
    private String nome;

    @Email(message = "E-mail inválido")
    private String email;

    @ValidCPF
    private String documento;

    private UserDTO(Builder builder) {
        this.id = builder.id;
        this.nome = builder.nome;
        this.email = builder.email;
        this.documento = builder.documento;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getDocumento() {
        return documento;
    }

    public static class Builder {
        private String id;
        private String nome;
        private String email;
        private String documento;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder documento(String documento) {
            this.documento = documento;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(this);
        }
    }
}


