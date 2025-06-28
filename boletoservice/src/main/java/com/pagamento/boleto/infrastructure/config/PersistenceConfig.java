package com.pagamento.boleto.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.pagamento.boleto.infrastructure.persistence",
    entityManagerFactoryRef = "jpaEntityManagerFactory",
    transactionManagerRef = "jpaTransactionManager"
)
@Profile("!mongo")
public class PersistenceConfig {

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false); // Desabilitado para usar Flyway
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.pagamento.boleto.domain.model");
        factory.setDataSource(dataSource);
        return factory;
    }

    @Bean
    @Primary
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "mongo")
@EnableMongoRepositories(
    basePackages = "com.pagamento.boleto.infrastructure.adapters.repository.mongo"
)
public class MongoConfig {
    // Configuração específica para MongoDB
    // Pode ser deixada vazia se apenas ativação de repositórios for necessária
}