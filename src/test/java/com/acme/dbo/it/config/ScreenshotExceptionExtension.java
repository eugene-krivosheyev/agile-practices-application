package com.acme.dbo.it.config;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static java.nio.file.Files.copy;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ScreenshotExceptionExtension implements TestExecutionExceptionHandler {
    @Override
    public void handleTestExecutionException(ExtensionContext testExecutionContext, Throwable error) throws Throwable {
        try {
            makeScreenshot(
                    testExecutionContext.getRequiredTestMethod().getName(),
                    getWebDriverFieldFromTestCaseObject(testExecutionContext.getRequiredTestInstance())
            );
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            error.addSuppressed(e);
        } finally {
            throw error;
        }
    }

    private void makeScreenshot(String testMethodName, WebDriver webDriver) throws IOException {
        File screenshotsDir = Paths.get("target", "test-error-reports").toFile();
        if (!screenshotsDir.exists()) screenshotsDir.mkdirs();

        copy(
                webDriver.findElement(By.tagName("body")).getScreenshotAs(OutputType.FILE).toPath(),
                new File(screenshotsDir, testMethodName + "-" + LocalDateTime.now() + ".jpg").toPath(),
                REPLACE_EXISTING
        );
    }

    private WebDriver getWebDriverFieldFromTestCaseObject(Object testCase) throws NoSuchFieldException, IllegalAccessException {
        Field webDriverField = getWebDriverField(testCase.getClass().getDeclaredFields());
        webDriverField.setAccessible(true);
        return (WebDriver) webDriverField.get(testCase);
    }

    private Field getWebDriverField(Field[] fields) throws NoSuchFieldException {
        for (Field field : fields) {
            if (field.getType().equals(WebDriver.class)) return field;
        }
        throw new NoSuchFieldException("type: WebDriver");
    }
}
