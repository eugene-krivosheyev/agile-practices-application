package com.acme.dbo.it.client.uat.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byClassName;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static java.lang.Integer.parseInt;

public class GetOperationBlock extends HtmlElement {
    @FindBy(xpath = ".//tr[@class = 'response']/td[contains(@class, 'response-col_status')]/text()")
    private WebElement serverResponseCode;

    public GetOperationBlock tryItOut() {
        $(withText("Try it out")).shouldBe(visible).click();
        $(withText("Execute")).shouldBe(visible).click();
        return this;
    }

    public int getSuccessCode() {
        String serverResponseCode =
                $(byClassName("response")).shouldBe(visible)
                    .$(byClassName("response-col_status")).shouldBe(visible)
                        .getText();

        return parseInt(serverResponseCode);
    }
}
