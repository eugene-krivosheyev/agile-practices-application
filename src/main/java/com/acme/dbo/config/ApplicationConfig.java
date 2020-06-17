package com.acme.dbo.config;

import com.acme.dbo.config.metrics.HttpMetricsTracker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import liquibase.exception.DatabaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.emptySet;
import static lombok.AccessLevel.PRIVATE;

@Configuration
@ComponentScan("com.acme")
@EnableWebSecurity
@EnableScheduling
@FieldDefaults(level = PRIVATE)
@Slf4j
public class ApplicationConfig extends WebSecurityConfigurerAdapter {
    @Autowired HttpPoolProperties httpPoolProperties;
    @Autowired private HttpMetricsTracker httpMetricsTracker;
    PoolingHttpClientConnectionManager apacheHttpClientConnectionManager;


    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.cors().disable().csrf().disable();
        security.httpBasic().disable();
    }

    @Autowired
    public void configureJacksonMapper(ObjectMapper jackson2ObjectMapper) {
        //placeholder for future customizations
    }

    @Bean
    public RestTemplate connectionPooledRestTemplate(RestTemplateBuilder builder) {
        Registry<ConnectionSocketFactory> apacheHttpClientConfigRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
            .build();

        apacheHttpClientConnectionManager = new PoolingHttpClientConnectionManager(apacheHttpClientConfigRegistry);
        apacheHttpClientConnectionManager.setMaxTotal(httpPoolProperties.getMaxTotal());
        apacheHttpClientConnectionManager.setDefaultMaxPerRoute(httpPoolProperties.getDefaultMaxPerRoute());
        apacheHttpClientConnectionManager.setValidateAfterInactivity(httpPoolProperties.getValidateAfterInactivity());

        RequestConfig apacheHttpClientRequestConfig = RequestConfig.custom()
                .setSocketTimeout(httpPoolProperties.getSocketTimeout())
                .setConnectTimeout(httpPoolProperties.getConnectTimeout())
                .setConnectionRequestTimeout(httpPoolProperties.getConnectionRequestTimeout())
            .build();

        final CloseableHttpClient apacheHttpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(apacheHttpClientRequestConfig)
                .setConnectionManager(apacheHttpClientConnectionManager)
            .build();

        return builder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(apacheHttpClient))
            .build();
    }

    @Scheduled(fixedRate = 20_000)
    public void updateApacheHttpClientMetrics() {
        if (apacheHttpClientConnectionManager == null) return; //NPE while tests

        Set<HttpRoute> routes = apacheHttpClientConnectionManager.getRoutes();
        for (HttpRoute route : routes) {
            httpMetricsTracker.add(route, apacheHttpClientConnectionManager);
        }
    }

    @Autowired
    public void configureMetrics(MeterRegistry registry) {
        new ClassLoaderMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);

//        new ExecutorServiceMetrics(executor, "thread-pool-executor", emptySet()).bindTo(registry);
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
