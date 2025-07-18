package com.pagamento.pix.domain.model;

/**
 * Representa os possíveis estados de uma transação PIX.
 * 
 * <p>Os status seguem o fluxo típico de uma transação:</p>
 * <ol>
 *   <li>EM_PROCESSAMENTO: Transação recebida e em processamento inicial</li>
 *   <li>VALIDANDO: Em processo de validação de dados</li>
 *   <li>VALIDADO: Dados aprovados na validação</li>
 *   <li>REJEITADO: Rejeitado na validação</li>
 *   <li>ENVIANDO_BACEN: Em processo de envio ao BACEN</li>
 *   <li>PROCESSADO: Confirmado pelo BACEN</li>
 *   <li>ERRO: Falha durante o processamento</li>
 *   <li>ESTORNANDO: Em processo de estorno</li>
 *   <li>ESTORNADO: Estorno confirmado</li>
 *   <li>ERRO_ESTORNO: Falha durante o estorno</li>
 * </ol>
 */
public enum PixStatus {
    
    EM_PROCESSAMENTO("Em processamento"),
    VALIDANDO("Validando informações"),
    VALIDADO("Dados validados"),
    REJEITADO("Transação rejeitada"),
    ENVIANDO_BACEN("Enviando ao BACEN"),
    PROCESSADO("Processado com sucesso"),
    ERRO("Erro no processamento"),
    ESTORNANDO("Estornando transação"),
    ESTORNADO("Transação estornada"),
    ERRO_ESTORNO("Erro no estorno");

    private final String descricao;

    PixStatus(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição amigável do status.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Verifica se o status indica uma transação concluída com sucesso.
     */
    public boolean isConcluido() {
        return this == PROCESSADO || this == ESTORNADO;
    }

    /**
     * Verifica se o status indica uma transação rejeitada ou com erro.
     */
    public boolean isErro() {
        return this == REJEITADO || this == ERRO || this == ERRO_ESTORNO;
    }

    /**
     * Verifica se o status permite estorno.
     */
    public boolean permiteEstorno() {
        return this == PROCESSADO;
    }

    /**
     * Verifica se o status é terminal (não permite mais alterações).
     */
    public boolean isTerminal() {
        return isConcluido() || isErro();
    }
}