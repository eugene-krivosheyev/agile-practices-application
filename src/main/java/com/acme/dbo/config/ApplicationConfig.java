package com.acme.dbo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.exception.DatabaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

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
    @Autowired private HttpPoolProperties httpPoolProperties;

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.cors().disable().csrf().disable();
        security.httpBasic().disable();
    }

    @Autowired
    public void configureJacksonMapper(ObjectMapper jackson2ObjectMapper) {
        //placeholder for future customizations
    }

    @Bean @Lazy
    public RestTemplate simplePrototypedRestTemplate(RestTemplateBuilder builder) {
        log.info("simplePrototypedRestTemplate built");
        return builder.build();
    }

    @Bean @Lazy
    public RestTemplate connectionPooledRestTemplate() {
        log.info("connectionPooledRestTemplate built");
        return new RestTemplate(httpRequestFactory());
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
            .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(httpPoolProperties.getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(httpPoolProperties.getDefaultMaxPerRoute());
        connectionManager.setValidateAfterInactivity(httpPoolProperties.getValidateAfterInactivity());
        RequestConfig requestConfig = RequestConfig.custom()
                //The time for the server to return data (response) exceeds the throw of read timeout
                .setSocketTimeout(httpPoolProperties.getSocketTimeout())
                //The time to connect to the server (handshake succeeded) exceeds the throw connect timeout
                .setConnectTimeout(httpPoolProperties.getConnectTimeout())
                //The timeout to get the connection from the connection pool. If the connection is not available after the timeout, the following exception will be thrown
                // org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .setConnectionRequestTimeout(httpPoolProperties.getConnectionRequestTimeout())
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
            .build();
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
