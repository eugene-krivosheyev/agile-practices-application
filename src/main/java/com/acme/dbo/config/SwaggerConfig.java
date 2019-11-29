package com.acme.dbo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Predicates.not;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@Configuration
@EnableSwagger2
@Profile("!test")
public class SwaggerConfig {
    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(asList("application/json"));

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES)
                .select()
                    .apis(basePackage("com.acme.dbo"))
                    .apis(not(basePackage("com.acme.dbo.config")))
                .paths(PathSelectors.any())
            .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
            "ACME DBO API",
            "",
            "1.0",
            "",
            new springfox.documentation.service.Contact("Eugene Krivosheyev", "", "ekr@bk.ru"),
            "All rights reserved. (C) ACME CORP.", "",
            emptyList()
        );
    }
}