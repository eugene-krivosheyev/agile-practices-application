package com.acme.dbo.it.client.uat;

import com.acme.dbo.config.ScreenshotExceptionExtension;
import com.acme.dbo.it.client.uat.page.MainPage;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

@DisabledIf(expression = "#{environment['features.client'] == 'false'}", loadContext = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(ScreenshotExceptionExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("it")
@Slf4j
@FieldDefaults(level = PRIVATE)

@Disabled
public class ClientUatIT {
    @LocalServerPort int serverPort;
    @Autowired WebDriver driver;


    @AfterAll
    public void closeBrowser() {
        driver.quit();
    }

    @Test
    public void shouldGetClientsWhenPrepopulatedDbHasSome() throws InterruptedException {
        final int successCode = new MainPage(driver, serverPort)
            .expandClientController()
            .expandGetOperation()
            .tryItOut()
            .getSuccessCode();

        assertThat(successCode)
                .isEqualTo(200);
    }
}
