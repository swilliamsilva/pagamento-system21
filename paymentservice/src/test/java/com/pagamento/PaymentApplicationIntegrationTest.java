package com.pagamento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class PaymentApplicationIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0"))
        .withExposedPorts(27017);

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // Configura MongoDB
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        
        // Configura Kafka
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    void applicationStartsWithAllContainersRunning() {
        assertTrue(mongoDBContainer.isRunning(), "MongoDB container should be running");
        assertTrue(kafkaContainer.isRunning(), "Kafka container should be running");
        // Teste adicional pode verificar conexão com serviços externos
    }
}