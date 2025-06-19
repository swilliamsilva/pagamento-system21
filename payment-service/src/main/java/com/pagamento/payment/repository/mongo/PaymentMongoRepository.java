package com.pagamento.payment.repository.mongo;

import com.pagamento.payment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMongoRepository extends MongoRepository<Payment, String> {{
}}
