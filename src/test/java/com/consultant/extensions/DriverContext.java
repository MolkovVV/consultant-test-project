package com.consultant.extensions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

public final class DriverContext {

    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(DriverContext.class);

    private static final String DRIVER_KEY = "driver";

    private DriverContext() {
    }

    public static ExtensionContext.Store store(ExtensionContext context) {
        return context.getStore(NAMESPACE);
    }

    public static WebDriver getDriver(ExtensionContext context) {
        return store(context).get(DRIVER_KEY, WebDriver.class);
    }

    public static void setDriver(ExtensionContext context, WebDriver driver) {
        store(context).put(DRIVER_KEY, driver);
    }

    public static void resetDriver(ExtensionContext context) {
        WebDriver current = getDriver(context);
        if (current != null) {
            current.quit();
        }
    }
}
