/* ========================================================
# Interface: PixDynamoRepository
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Repositório Spring Data para persistência de transações Pix no DynamoDB.
# ======================================================== */

package com.pagamento.pix.repository.dynamo;

import com.pagamento.pix.domain.model.Pix;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@EnableScan  // Necessário para permitir varredura (scan) de tabelas no Dynamo
@Repository
public interface PixDynamoRepository extends CrudRepository<Pix, String> {

	com.pagamento.pix.domain.model.Pix salvar(com.pagamento.pix.domain.model.Pix pixCriptografado);
    // Adicione métodos customizados aqui se necessário, como findByChaveDestino, etc.
}
