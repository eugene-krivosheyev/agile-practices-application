package com.acme.dbo.it.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import java.util.ArrayList;

import static java.lang.System.*;


@TestConfiguration
public class TestConfig {
    private ChromeDriver driver;
    
    @Lazy @Bean
    @Profile("it")
    /**
     * @Prototype //TODO make correct working singleton on Linux
     */
    public WebDriver webDriver() {
        //region Driver setup
        String chromeDriverEnvLocation = getenv("webdriver.chrome.driver");
        String chromeDriverPropLocation = getProperty("webdriver.chrome.driver");
        if (chromeDriverEnvLocation != null || chromeDriverPropLocation != null) {
            setProperty("webdriver.chrome.driver", chromeDriverEnvLocation != null ? chromeDriverEnvLocation : chromeDriverPropLocation);
        } else {
            WebDriverManager.chromiumdriver().setup();
        }

        driver = new ChromeDriver(new ChromeOptions()
                .addArguments("--headless")
                .addArguments("--disable-gpu")
                .addArguments("--disable-dev-shm-usage")
                .addArguments("--disable-extensions")
                .addArguments("--no-sandbox")
                .addArguments("--start-maximized")
                .addArguments("--ignore-certificate-errors")
        );
        driver.manage().window().maximize();
        //endregion

        //region Selenide setup
        WebDriverRunner.setWebDriver(driver);
        Configuration.timeout = 10_000;
        Configuration.reportsFolder = "target/surefire-reports";
        //endregion

        return driver;
    }
}
