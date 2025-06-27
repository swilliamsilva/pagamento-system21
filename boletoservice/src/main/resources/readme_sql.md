Para H2 (testes) e MySQL (produção):

    Tabela Principal: boleto

sql

CREATE TABLE boleto (
    id VARCHAR(36) PRIMARY KEY,
    pagador VARCHAR(255) NOT NULL,
    beneficiario VARCHAR(255) NOT NULL,
    valor DECIMAL(19,2) NOT NULL CHECK (valor > 0),
    data_emissao DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    status VARCHAR(20) NOT NULL,
    documento VARCHAR(255),
    instrucoes TEXT,
    local_pagamento VARCHAR(255),
    codigo_barras VARCHAR(255) NOT NULL,
    linha_digitavel VARCHAR(255) NOT NULL,
    qr_code TEXT NOT NULL,
    nosso_numero VARCHAR(50) NOT NULL,
    id_externo VARCHAR(255),
    boleto_original_id VARCHAR(36),
    reemissoes INT DEFAULT 0,
    motivo_cancelamento TEXT,
    data_ultima_atualizacao TIMESTAMP NOT NULL,
    FOREIGN KEY (boleto_original_id) REFERENCES boleto(id)
);

    Tabela de Histórico: boleto_historico_status

sql

CREATE TABLE boleto_historico_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    boleto_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    FOREIGN KEY (boleto_id) REFERENCES boleto(id)
);

    Índices para Melhor Performance:

sql

CREATE INDEX idx_boleto_status ON boleto(status);
CREATE INDEX idx_boleto_data_vencimento ON boleto(data_vencimento);
CREATE INDEX idx_boleto_historico_boleto_id ON boleto_historico_status(boleto_id);