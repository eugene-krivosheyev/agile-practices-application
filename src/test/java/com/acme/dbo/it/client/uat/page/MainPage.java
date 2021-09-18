package com.acme.dbo.it.client.uat.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.*;

public class MainPage {
    private WebDriver driver;
    private String mainPageUrl;

    @FindBy(id = "operations-tag-client-controller")
    private ClientControllerBlock clientControllerBlock;

    public MainPage(WebDriver driver, int serverPort) {
        this.driver = driver;
        this.mainPageUrl = "http://localhost:" + serverPort + "/dbo/swagger-ui/";

        driver.get(mainPageUrl);
        $(className("models")).shouldBe(visible)
                .$(byText("Models")).shouldBe(visible);

        HtmlElementLoader.populatePageObject(this, driver);
    }

    public ClientControllerBlock expandClientController() {
        $(id("operations-tag-client-controller")).click();
        $(linkText("/dbo/api/client")).shouldBe(visible);
        return clientControllerBlock;
    }
}
