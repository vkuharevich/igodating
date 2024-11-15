package com.igodating.questionary.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = { "com.igodating.questionary.repository"  })
@EnableTransactionManagement
public class DbConfig {

    private static final String CHANGELOG_PATH = "classpath:liquibase/db.changelog-master.xml";

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        hibernateJpaVendorAdapter.setShowSql(true);

        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaProperties jpaProperties) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource);
        factory.setPackagesToScan(
                "com.igodating.questionary.model"
        );
        factory.setJpaVendorAdapter(jpaVendorAdapter());
        factory.setPersistenceUnitName("default");

        Properties jpaProps = new Properties();
        jpaProps.putAll(jpaProperties.getProperties());
        factory.setJpaProperties(jpaProps);

        return factory;
    }

    @Bean
    public TransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean
    public SpringLiquibase springLiquibase(DataSource dataSource) {
        SpringLiquibase springLiquibase = new SpringLiquibase();
        springLiquibase.setChangeLog(CHANGELOG_PATH);
        springLiquibase.setDataSource(dataSource);
        springLiquibase.setDefaultSchema(schema);
        springLiquibase.setLiquibaseSchema(schema);

        return springLiquibase;
    }
}
