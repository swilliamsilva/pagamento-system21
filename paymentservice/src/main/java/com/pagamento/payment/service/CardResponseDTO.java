
import com.pagamento.card.domain.enums.PaymentStatus;
import java.math.BigDecimal;

public class CardResponseDTO {
    private String transactionId;
    private String bandeira;
    private PaymentStatus status;
    private BigDecimal valor;
    private String codigoAutorizacao;
    private String mensagem;

    public CardResponseDTO(String transactionId, 
                          String bandeira, 
                          PaymentStatus status, 
                          BigDecimal valor, 
                          String codigoAutorizacao,
                          String mensagem) {
        this.transactionId = transactionId;
        this.bandeira = bandeira;
        this.status = status;
        this.valor = valor;
        this.codigoAutorizacao = codigoAutorizacao;
        this.mensagem = mensagem;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getBandeira() { return bandeira; }
    public PaymentStatus getStatus() { return status; }
    public BigDecimal getValor() { return valor; }
    public String getCodigoAutorizacao() { return codigoAutorizacao; }
    public String getMensagem() { return mensagem; }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}