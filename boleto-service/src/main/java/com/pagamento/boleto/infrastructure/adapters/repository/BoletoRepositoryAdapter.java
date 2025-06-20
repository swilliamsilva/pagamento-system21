// src/main/java/com/pagamento/boleto/infrastructure/adapters/repository/BoletoRepositoryAdapter.java
// Para persistência real, você poderá usar Mongo ou PostgreSQL e adaptar para um JpaRepository ou MongoRepository real.

package com.pagamento.boleto.infrastructure.adapters.repository;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BoletoRepositoryAdapter implements BoletoRepositoryPort {

    private final Map<String, Boleto> fakeDatabase = new HashMap<>();

    @Override
    public Optional<Boleto> findById(String id) {
        return Optional.ofNullable(fakeDatabase.get(id));
    }

    @Override
    public Boleto save(Boleto boleto) {
        fakeDatabase.put(boleto.getId(), boleto);
        return boleto;
    }

    @Override
    public List<Boleto> findAll() {
        return new ArrayList<>(fakeDatabase.values());
    }

    @Override
    public void deleteById(String id) {
        fakeDatabase.remove(id);
    }
}
