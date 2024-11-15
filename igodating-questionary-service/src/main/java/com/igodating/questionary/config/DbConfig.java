package com.igodating.questionary.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    private static final String CHANGELOG_PATH = "classpath:liquibase/db.changelog-master.xml";

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

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
