// src/main/java/com/pagamento/payment/repository/mongo/PaymentMongoRepository.java
package com.pagamento.payment.repository.mongo;

import com.pagamento.payment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMongoRepository extends MongoRepository<Payment, String> {
    // Você pode adicionar consultas personalizadas aqui, ex:
    // List<Payment> findByUserId(String userId);
}
