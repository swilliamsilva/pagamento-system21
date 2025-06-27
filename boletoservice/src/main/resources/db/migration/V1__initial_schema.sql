-- Tabela de boletos
CREATE TABLE IF NOT EXISTS boleto (
    id VARCHAR(36) PRIMARY KEY,
    pagador VARCHAR(255) NOT NULL,
    beneficiario VARCHAR(255) NOT NULL,
    valor DECIMAL(19,2) NOT NULL,
    data_emissao DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    status VARCHAR(20) NOT NULL,
    documento VARCHAR(255),
    instrucoes TEXT,
    local_pagamento VARCHAR(255),
    
    -- Dados técnicos
    codigo_barras VARCHAR(255) NOT NULL,
    linha_digitavel VARCHAR(255) NOT NULL,
    qr_code TEXT NOT NULL,
    nosso_numero VARCHAR(50) NOT NULL,
    
    -- Controle externo e relacionamentos
    id_externo VARCHAR(255),
    boleto_original_id VARCHAR(36),
    
    -- Contadores e motivos
    reemissoes INT DEFAULT 0,
    motivo_cancelamento TEXT,
    
    -- Auditoria
    data_ultima_atualizacao TIMESTAMP NOT NULL,
    
    -- Constraints
    CONSTRAINT chk_valor_positivo CHECK (valor > 0),
    FOREIGN KEY (boleto_original_id) REFERENCES boleto(id)
);

-- Tabela de histórico de status
CREATE TABLE IF NOT EXISTS boleto_historico_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    boleto_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    FOREIGN KEY (boleto_id) REFERENCES boleto(id)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_boleto_status ON boleto(status);
CREATE INDEX IF NOT EXISTS idx_boleto_data_vencimento ON boleto(data_vencimento);
CREATE INDEX IF NOT EXISTS idx_boleto_original ON boleto(boleto_original_id);
CREATE INDEX IF NOT EXISTS idx_historico_boleto ON boleto_historico_status(boleto_id);