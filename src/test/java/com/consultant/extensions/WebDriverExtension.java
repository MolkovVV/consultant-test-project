package com.consultant.extensions;

import com.consultant.pages.WebDriverFactory;
import com.consultant.pages.helpers.BaseTest;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class WebDriverExtension implements BeforeEachCallback, AfterEachCallback, TestWatcher {

    @Override
    public void beforeEach(ExtensionContext context) {
        WebDriver driver = WebDriverFactory.createDriver();
        DriverContext.setDriver(context, driver);
        context.getTestInstance()
                .filter(BaseTest.class::isInstance)
                .map(BaseTest.class::cast)
                .ifPresent(baseTest -> baseTest.initPages(driver));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        WebDriver driver = DriverContext.getDriver(context);
        if (driver != null) {
            driver.quit();
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        attachDiagnostics(DriverContext.getDriver(context), cause);
    }

    static void attachDiagnostics(WebDriver driver, Throwable cause) {
        if (driver == null) {
            return;
        }
        Allure.addAttachment("Причина падения", "text/plain", cause.toString());
        try {
            Allure.addAttachment("URL", "text/plain", driver.getCurrentUrl());
            Allure.addAttachment("Title", "text/plain", driver.getTitle());
            Allure.addAttachment("Page source", "text/html", driver.getPageSource(), ".html");
        } catch (Exception ignored) {
            // driver session may already be invalid
        }
        if (driver instanceof TakesScreenshot screenshot) {
            try {
                byte[] bytes = screenshot.getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Скриншот при падении", "image/png",
                        new ByteArrayInputStream(bytes), ".png");
            } catch (Exception ignored) {
            }
        }
        attachBrowserLogs(driver);
    }

    private static void attachBrowserLogs(WebDriver driver) {
        try {
            LogEntries entries = driver.manage().logs().get(LogType.BROWSER);
            if (entries == null || entries.getAll().isEmpty()) {
                return;
            }
            StringBuilder builder = new StringBuilder();
            entries.forEach(entry -> builder
                    .append('[').append(entry.getLevel()).append("] ")
                    .append(entry.getMessage()).append('\n'));
            Allure.addAttachment("Browser console", "text/plain",
                    new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8)), ".txt");
        } catch (Exception ignored) {
        }
    }
}
