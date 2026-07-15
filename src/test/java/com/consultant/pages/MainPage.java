package com.consultant.pages;

import com.consultant.config.ConfigHolder;
import com.consultant.utils.XPathUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Стартовая страница consultant.ru (главное меню, быстрый поиск, результаты).
 */
public class MainPage extends BasePage {

    private static final By QUICK_SEARCH_BUTTON =
            By.cssSelector("a[data-title=\"Быстрый поиск документов по всем разделам\"]");
    private static final By SEARCH_INPUT = By.cssSelector("input.x-page-components-search-panel__filter");
    private static final By SEARCH_BUTTON = By.xpath("//button[text()='Найти']");
    private static final By SEARCH_RESULT_TITLE = By.cssSelector("a.x-page-components-search-result-item__extra-title");

    public MainPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открыть страницу consultant.ru")
    public MainPage open() {
        driver.get(ConfigHolder.config().baseUrl());
        return this;
    }

    @Step("Кликнуть «Быстрый поиск»")
    public MainPage clickQuickSearch() {
        click(QUICK_SEARCH_BUTTON);
        return this;
    }

    @Step("Ввести «{text}» в поиск")
    public MainPage typeSearchQuery(String text) {
        type(SEARCH_INPUT, text);
        return this;
    }

    @Step("Убедиться, что кнопка «Быстрый поиск» отображается")
    public MainPage assertQuickSearchIsDisplayed() {
        assertTrue(isDisplayed(QUICK_SEARCH_BUTTON), "Кнопка «Быстрый поиск» не отображается");
        return this;
    }

    @Step("Нажать «Найти»")
    public MainPage clickSearch() {
        click(SEARCH_BUTTON);
        wait.until(ExpectedConditions.presenceOfElementLocated(SEARCH_RESULT_TITLE));
        return this;
    }

    @Step("Поиск «{query}»")
    public MainPage search(String query) {
        typeSearchQuery(query);
        return clickSearch();
    }

    @Step("Получить полное название документа «{titleFragment}» из результатов")
    public String getDocumentFullTitle(String titleFragment) {
        By locator = By.xpath(
                "//a[contains(@class,'x-page-components-search-result-item__extra-title')"
                        + " and contains(normalize-space(.), " + XPathUtils.literal(titleFragment) + ")]"
        );
        return waitForVisible(locator).getText().trim();
    }

    @Step("Открыть документ «{titleFragment}» из результатов")
    public DocumentPage openDocumentByTitle(String titleFragment) {
        return openDocumentByTitle(titleFragment, DocumentPage.class);
    }

    @Step("Открыть документ «{titleFragment}» из результатов")
    public <T extends BasePage> T openDocumentByTitle(String titleFragment, Class<T> pageClass) {
        Set<String> oldWindows = driver.getWindowHandles();

        By locator = By.xpath(
                "//a[contains(@class,'x-page-components-search-result-item__extra-title')"
                        + " and contains(normalize-space(.), " + XPathUtils.literal(titleFragment) + ")]"
        );
        click(locator);

        wait.until(ExpectedConditions.numberOfWindowsToBe(oldWindows.size() + 1));

        Set<String> newWindows = driver.getWindowHandles();
        newWindows.removeAll(oldWindows);
        driver.switchTo().window(newWindows.iterator().next());

        try {
            return pageClass.getConstructor(WebDriver.class).newInstance(driver);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Не удалось создать " + pageClass.getSimpleName(), e);
        }
    }
}
