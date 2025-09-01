package com.pagamento.common.request;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private AuthRequest(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder {
        private String username;
        private String password;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public AuthRequest build() {
            return new AuthRequest(this);
        }
    }
}


