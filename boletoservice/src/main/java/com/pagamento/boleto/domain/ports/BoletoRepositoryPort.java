package com.pagamento.boleto.domain.ports;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BoletoRepositoryPort {

    /**
     * Salva um novo boleto no repositório
     * 
     * @param boleto Boleto a ser persistido
     * @return Boleto salvo com ID gerado
     */
    Boleto salvar(Boleto boleto);
    
    /**
     * Atualiza um boleto existente
     * 
     * @param boleto Boleto com dados atualizados
     * @return Boleto atualizado
     */
    Boleto atualizar(Boleto boleto);
    
    /**
     * Busca um boleto pelo ID
     * 
     * @param id ID do boleto
     * @return Optional contendo o boleto se encontrado
     */
    Optional<Boleto> buscarPorId(String id);
    
    /**
     * Busca todos os boletos
     * 
     * @return Lista de todos os boletos
     */
    List<Boleto> buscarTodos();
    
    /**
     * Busca boletos por status
     * 
     * @param status Status desejado
     * @return Lista de boletos com o status especificado
     */
    List<Boleto> buscarPorStatus(BoletoStatus status);
    
    /**
     * Busca boletos vencidos até uma data específica
     * 
     * @param dataLimite Data limite para vencimento
     * @return Lista de boletos vencidos
     */
    List<Boleto> buscarVencidos(LocalDate dataLimite);
    
    /**
     * Busca boletos por documento do pagador
     * 
     * @param documento Documento do pagador
     * @return Lista de boletos associados ao documento
     */
    List<Boleto> buscarPorDocumentoPagador(String documento);
    
    /**
     * Busca boletos por intervalo de data de vencimento
     * 
     * @param inicio Data inicial do intervalo
     * @param fim Data final do intervalo
     * @return Lista de boletos com vencimento no intervalo
     */
    List<Boleto> buscarPorVencimentoEntre(LocalDate inicio, LocalDate fim);
    
    /**
     * Exclui um boleto pelo ID
     * 
     * @param id ID do boleto a ser excluído
     */
    void deletarPorId(String id);
}