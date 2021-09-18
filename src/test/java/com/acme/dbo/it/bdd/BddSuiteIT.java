package com.acme.dbo.it.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("it")
@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "pretty",
                "html:target/cucumber-html-reports/report.html",
                "json:target/cucumber-html-reports/cucumber.json",
                "junit:target/cucumber-html-reports/cucumber.xml"},
        tags = "not @WIP"
)
public class BddSuiteIT {
}
