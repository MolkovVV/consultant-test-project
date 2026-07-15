package com.consultant.pages;

import com.consultant.pages.helpers.FrameHelper;
import com.consultant.utils.XPathUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Страница открытого документа (включая iframe и панель поиска).
 */
public class DocumentPage extends BasePage {

    private static final By SEARCH_INPUT = By.cssSelector("input.x-input__field");
    private static final By DOCUMENT_TITLE = By.cssSelector(".documentTitle");
    private static final By YELLOW_ARROW = By.cssSelector(".scroll-marker, .highlight-arrow, [class*='yellow']");

    public DocumentPage(WebDriver driver) {
        super(driver);
    }

    @Step("Проверить, что название документа совпадает с «{expectedTitle}»")
    public DocumentPage assertDocumentTitle(String expectedTitle) {
        String actual = FrameHelper.inDocumentFrame(driver, (Function<WebDriver, String>) d ->
                wait.until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_TITLE)).getText().trim()
        );
        String normalizedExpected = expectedTitle.replaceAll("\\s+", " ").trim();
        String normalizedActual = actual.replaceAll("\\s+", " ").trim();
        assertEquals(normalizedExpected, normalizedActual, "Название документа не совпадает");
        return this;
    }

    @Step("Убедиться, что в поле поиска отображается запрос «{query}»")
    public DocumentPage assertSearchInputContainsQuery(String query) {
        wait.until(d -> {
            WebElement input = d.findElement(SEARCH_INPUT);
            String value = (String) ((JavascriptExecutor) d)
                    .executeScript("return arguments[0].value;", input);
            return query.equals(value);
        });
        return this;
    }

    @Step("Проскроллить документ к «{title}»")
    public DocumentPage scrollToTitle(String title) {
        FrameHelper.inDocumentFrame(driver, d -> {
            By locator = By.xpath(
                    "//*[self::span and contains(normalize-space(.), " + XPathUtils.literal(title) + ")]"
            );
            WebElement element = d.findElement(locator);
            new Actions(d).moveToElement(element).perform();
        });
        return this;
    }

    @Step("Кликнуть по ссылке «{text}» в документе")
    public DocumentPage clickLinkInDocument(String text) {
        FrameHelper.inDocumentFrame(driver, d -> {
            By locator = By.xpath("//a[contains(normalize-space(.), " + XPathUtils.literal(text) + ")]");
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        });
        attachScreenshot("После клика: " + text);
        return this;
    }

    @Step("Проверить наличие жёлтой стрелки у абзаца")
    public DocumentPage assertYellowArrowIsVisible() {
        FrameHelper.inDocumentFrame(driver, d -> {
            WebDriverWait frameWait = new WebDriverWait(d, Duration.ofSeconds(config.timeoutSeconds()));
            frameWait.until(ExpectedConditions.visibilityOfElementLocated(YELLOW_ARROW));
        });
        return this;
    }

    @Step("Выделить абзац «{text}» и приложить текст к отчёту")
    public DocumentPage attachParagraphToReport(String text) {

        String content = FrameHelper.inDocumentFrame(driver, d -> {

            By locator = By.xpath(
                    "//div[@class='U' and contains(normalize-space(.), "
                            + XPathUtils.literal(text) + ")]"
            );

            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );

            // Забираем текст ДО любых действий, которые могут обновить DOM
            String paragraphText = element.getText();

            highlightElement(element);

            if (d instanceof JavascriptExecutor js) {
                js.executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});",
                        element
                );
            }

            attachScreenshot("Выделенный абзац: " + text);

            return paragraphText;
        });

        Allure.addAttachment(
                "Текст абзаца",
                "text/plain",
                content
        );

        return this;
    }
}
