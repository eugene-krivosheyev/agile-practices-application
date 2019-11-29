package com.acme.dbo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.exception.DatabaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Configuration
@EnableWebSecurity
@ComponentScan("com.acme")
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    public void configureJackson(ObjectMapper jackson2ObjectMapper) {
        //placeholder for future customizations
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.cors().disable().csrf().disable();
        security.httpBasic().disable();
    }

    @Bean
    @Profile("qa | prod")
    @DependsOn("liquibase")
    @Lazy(false)
    public PoolClosingLiquibaseFinishedListener liquibaseDbUpdateFinishedListener() {
        log.debug("Liquibase finished updating db. Handling this event with PoolClosingLiquibaseFinishedListener...");
        return new PoolClosingLiquibaseFinishedListener();
    }

    static class PoolClosingLiquibaseFinishedListener {
        @Autowired private SpringLiquibase liquibase;

        @PostConstruct
        public void closeLiquibaseConnectionPool() throws DatabaseException {
            log.debug("Closing Liquibase Hikari connection pool...");
            DataSource liquibaseDatasource = liquibase.getDataSource();
            if (liquibaseDatasource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) liquibaseDatasource;
                hikariDataSource.close();
                log.debug("Closed Liquibase Hikari connection pool: " + hikariDataSource.getPoolName());
            }
        }
    }
}
