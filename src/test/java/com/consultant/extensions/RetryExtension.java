package com.consultant.extensions;

import com.consultant.config.ConfigHolder;
import com.consultant.pages.WebDriverFactory;
import com.consultant.pages.helpers.BaseTest;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Method;

/**
 * Повторный запуск упавшего UI-теста с новой сессией WebDriver.
 */
public class RetryExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(
            Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext
    ) throws Throwable {
        int retryCount = ConfigHolder.config().retryCount();
        Throwable lastFailure = null;

        for (int attempt = 0; attempt <= retryCount; attempt++) {
            try {
                invocation.proceed();
                return;
            } catch (Throwable failure) {
                lastFailure = failure;
                if (attempt < retryCount) {
                    int retryNumber = attempt + 1;
                    Allure.step("Retry " + retryNumber + " / " + retryCount + " после ошибки: "
                            + failure.getMessage());
                    restartSession(extensionContext);
                }
            }
        }
        throw lastFailure;
    }

    private void restartSession(ExtensionContext context) {
        DriverContext.resetDriver(context);
        WebDriver driver = WebDriverFactory.createDriver();
        DriverContext.setDriver(context, driver);
        context.getTestInstance()
                .filter(BaseTest.class::isInstance)
                .map(BaseTest.class::cast)
                .ifPresent(baseTest -> baseTest.initPages(driver));
    }
}
