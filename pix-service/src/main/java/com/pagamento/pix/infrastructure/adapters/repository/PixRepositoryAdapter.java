/* ========================================================
# Classe: PixRepositoryAdapter
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Adaptador de persistência usando DynamoDB.
# ======================================================== */

package com.pagamento.pix.infrastructure.adapters.repository;

import com.pagamento.pix.domain.ports.PixRepositoryPort;
import com.pagamento.pix.model.Pix;
import com.pagamento.pix.repository.dynamo.PixDynamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PixRepositoryAdapter implements PixRepositoryPort {

    @Autowired
    private PixDynamoRepository dynamoRepository;

    @Override
    public Optional<Pix> findById(String id) {
        return dynamoRepository.findById(id);
    }

    @Override
    public Pix save(Pix pix) {
        return dynamoRepository.save(pix);
    }

    @Override
    public void deleteById(String id) {
        dynamoRepository.deleteById(id);
    }
}
