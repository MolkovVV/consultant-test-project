package com.consultant.pages.helpers;

import com.consultant.extensions.RetryExtension;
import com.consultant.extensions.WebDriverExtension;
import com.consultant.pages.DocumentPage;
import com.consultant.pages.MainPage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@ExtendWith({WebDriverExtension.class, RetryExtension.class})
public abstract class BaseTest {

    protected WebDriver driver;
    protected MainPage mainPage;
    protected DocumentPage documentPage;

    public void initPages(WebDriver driver) {
        this.driver = driver;
        this.mainPage = new MainPage(driver);
        this.documentPage = new DocumentPage(driver);
    }
}
