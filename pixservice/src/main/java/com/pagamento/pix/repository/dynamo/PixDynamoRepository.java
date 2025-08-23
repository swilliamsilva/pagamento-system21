package com.pagamento.pix.repository.dynamo;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@EnableScan
@Repository
public interface PixDynamoRepository extends CrudRepository<PixDynamoEntity, String> {
    // Adicione métodos customizados aqui se necessário, como findByChaveDestino, etc.
}


