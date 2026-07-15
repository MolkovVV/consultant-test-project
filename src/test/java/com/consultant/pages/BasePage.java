package com.consultant.pages;

import com.consultant.config.ConfigHolder;
import com.consultant.config.TestConfig;
import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.ByteArrayInputStream;
import java.time.Duration;

public class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final TestConfig config;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigHolder.config();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.timeoutSeconds()));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected void attachScreenshot(String name) {
        if (driver instanceof TakesScreenshot screenshot) {
            byte[] bytes = screenshot.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), ".png");
        }
    }

    protected void highlightElement(WebElement element) {
        if (driver instanceof JavascriptExecutor js) {
            js.executeScript("arguments[0].style.outline='3px solid red'", element);
        }
    }
}
