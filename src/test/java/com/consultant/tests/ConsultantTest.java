package com.consultant.tests;

import com.consultant.config.TestDataHolder;
import com.consultant.pages.DocumentPage;
import com.consultant.pages.helpers.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Epic("Consultant.ru")
@Feature("Поиск и навигация по документам")
@Tag("smoke")
@Execution(ExecutionMode.CONCURRENT)
public class ConsultantTest extends BaseTest {

    @Test
    @DisplayName("Поиск документа и навигация по ссылкам")
    @Description("Быстрый поиск → открытие документа → проверка заголовка и поискового запроса → переход по ссылке")
    @Story("Налоговый кодекс — статья 145")
    void shouldSearchDocumentAndNavigateByLink() {
        var data = TestDataHolder.data();
        String documentTitle = data.documentTitle();

        mainPage.open()
                .assertQuickSearchIsDisplayed()
                .clickQuickSearch()
                .typeSearchQuery(data.searchQuery())
                .clickSearch();

        String fullTitle = mainPage.getDocumentFullTitle(documentTitle);

        DocumentPage documentPage = mainPage.openDocumentByTitle(documentTitle);
        documentPage.assertDocumentTitle(fullTitle)
                .assertSearchInputContainsQuery(data.searchQuery())
                .scrollToTitle(data.articleTitle())
                .clickLinkInDocument(data.linkText())
                .assertYellowArrowIsVisible()
                .attachParagraphToReport("Организации и индивидуальные предприниматели");
    }
}
