package com.consultant.pages.helpers;

import com.consultant.config.ConfigHolder;
import com.consultant.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Безопасное переключение в iframe документа с возвратом в default content.
 */
public final class FrameHelper {

    private static final By DOCUMENT_IFRAME = By.cssSelector("#mainContent iframe");

    private FrameHelper() {
    }

    public static void inDocumentFrame(WebDriver driver, Consumer<WebDriver> action) {
        inDocumentFrame(driver, d -> {
            action.accept(d);
            return null;
        });
    }

    public static <T> T inDocumentFrame(WebDriver driver, Function<WebDriver, T> action) {
        TestConfig config = ConfigHolder.config();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(config.timeoutSeconds()));
        driver.switchTo().frame(wait.until(ExpectedConditions.presenceOfElementLocated(DOCUMENT_IFRAME)));
        try {
            return action.apply(driver);
        } finally {
            driver.switchTo().defaultContent();
        }
    }
}
