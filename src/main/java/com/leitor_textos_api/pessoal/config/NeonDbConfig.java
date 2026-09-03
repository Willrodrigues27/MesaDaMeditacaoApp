package com.leitor_textos_api.pessoal.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;

//@Profile("!local")
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        entityManagerFactoryRef = "neonEntityManagerFactory",
//        transactionManagerRef = "neonTransactionManager",
//
//        basePackages = { "com.leitor_textos_api.pessoal.repository" },
//        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
//                type = org.springframework.context.annotation.FilterType.REGEX,
//                pattern = "com\\.leitor_textos_api\\.pessoal\\.repository\\.local\\..*"
//        )
//)
public class NeonDbConfig {

    @Primary
    @Bean(name = "neonProperties")
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "neonDataSource")
    public DataSource dataSource(@Qualifier("neonProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "neonEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("neonDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.leitor_textos_api.pessoal.modelo")
                .persistenceUnit("neon")
                .build();
    }

    @Primary
    @Bean(name = "neonTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("neonEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}