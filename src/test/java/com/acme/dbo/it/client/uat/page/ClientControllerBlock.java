package com.acme.dbo.it.client.uat.page;

import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;


public class ClientControllerBlock extends HtmlElement {
    @FindBy(id = "operations-client-controller-getClientsUsingGET")
    private GetOperationBlock getOperationBlock;

    public GetOperationBlock expandGetOperation() {
        $(id("operations-client-controller-getClientsUsingGET")).click();
        $(byText("Get all clients")).shouldBe(visible);

        return getOperationBlock;
    }
}
