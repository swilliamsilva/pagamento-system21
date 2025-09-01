package com.pagamento.common.response;

public class AuthResponse {
    private String token;
    private String role;

    private AuthResponse(Builder builder) {
        this.token = builder.token;
        this.role = builder.role;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public static class Builder {
        private String token;
        private String role;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(this);
        }
    }
}


