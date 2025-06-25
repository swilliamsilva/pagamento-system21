/* ========================================================
# Interface: PixDynamoRepository
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Repositório Spring Data para persistência de transações Pix no DynamoDB.
# ======================================================== */

package com.pagamento.pix.repository.dynamo;

import com.pagamento.pix.model.Pix;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@EnableScan  // Necessário para permitir varredura (scan) de tabelas no Dynamo
@Repository
public interface PixDynamoRepository extends CrudRepository<Pix, String> {
    // Adicione métodos customizados aqui se necessário, como findByChaveDestino, etc.
}
