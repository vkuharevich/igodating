package com.igodating.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

import static com.igodating.constant.Constants.QUESTIONARY_TYPE_CACHE;

@Configuration
@EnableScheduling
@EnableCaching
public class ApplicationConfig {

    private static final String CHANGELOG_PATH = "classpath:liquibase/db.changelog-master.xml";

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(QUESTIONARY_TYPE_CACHE);
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
