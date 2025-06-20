// src/main/java/com/pagamento/pix/repository/dynamo/PixDynamoRepository.java
package com.pagamento.pix.repository.dynamo;

import com.pagamento.pix.model.Pix;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
@EnableScan
public interface PixDynamoRepository extends CrudRepository<Pix, String> {
    // Consultas customizadas podem ser adicionadas se necessário
}
// Nota: Certifique-se de que o Pix esteja anotado com @DynamoDBTable(tableName = "pix").
// TODO: Implement PixDynamoRepository.java