package com.pagamento.common.dto;

public class CardDTO {
    private String id;
    private String value;

    private CardDTO(Builder builder) {
        this.id = builder.id;
        this.value = builder.value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {
        private String id;
        private String value;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public CardDTO build() {
            return new CardDTO(this);
        }
    }
}


