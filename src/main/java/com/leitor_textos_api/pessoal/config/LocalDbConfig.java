package com.leitor_textos_api.pessoal.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;

//@Profile("local")
//@Configuration
//@EnableJpaRepositories(
//        entityManagerFactoryRef = "localEntityManagerFactory",
//        transactionManagerRef = "localTransactionManager",
//        basePackages = { "com.leitor_textos_api.pessoal.repository.local" }
//)
public class LocalDbConfig {

    @Value("${spring.datasource.local.url:jdbc:postgresql://localhost:5432/db_local}")
    private String url;

    @Value("${spring.datasource.local.username:postgres}")
    private String username;

    @Value("${spring.datasource.local.password:postgres}")
    private String password;

    @Bean(name = "localProperties")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl(url);
        properties.setUsername(username);
        properties.setPassword(password);
        properties.setDriverClassName("org.postgresql.Driver");
        return properties;
    }

    @Bean(name = "localDataSource")
    public DataSource dataSource(@Qualifier("localProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "localEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("localDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.leitor_textos_api.pessoal.modelo")
                .persistenceUnit("local")
                .build();
    }

    @Bean(name = "localTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("localEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}