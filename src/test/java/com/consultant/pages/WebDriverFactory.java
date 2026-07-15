package com.consultant.pages;

import com.consultant.config.Browser;
import com.consultant.config.BrowserBinaryResolver;
import com.consultant.config.ConfigHolder;
import com.consultant.config.TestConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;

public final class WebDriverFactory {

    private static final TestConfig CONFIG = ConfigHolder.config();

    private WebDriverFactory() {
    }

    public static WebDriver createDriver() {
        WebDriver driver = createConfiguredDriver(CONFIG.browser());
        applyTimeouts(driver);
        setWindowSize(driver);
        return driver;
    }

    public static WebDriver createConfiguredDriver(Browser browser) {
        return switch (browser) {
            case CHROME -> buildChrome();
            case EDGE -> buildEdge();
            case FIREFOX -> buildFirefox();
            case REMOTE_CHROME -> buildRemote(new ChromeOptions());
            case REMOTE_EDGE -> buildRemote(new EdgeOptions());
            case REMOTE_FIREFOX -> buildRemote(new FirefoxOptions());
        };
    }

    private static WebDriver buildChrome() {
        WebDriverManager.chromedriver().browserVersion(resolveVersion()).setup();
        ChromeOptions options = new ChromeOptions();
        applyBinary(options, Browser.CHROME);
        applyCommon(options);
        applyHeadless(options);
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        return new ChromeDriver(options);
    }

    private static WebDriver buildEdge() {
        WebDriverManager.edgedriver().browserVersion(resolveVersion()).setup();
        EdgeOptions options = new EdgeOptions();
        applyBinary(options, Browser.EDGE);
        applyCommon(options);
        applyHeadless(options);
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        return new EdgeDriver(options);
    }

    private static WebDriver buildFirefox() {
        WebDriverManager.firefoxdriver().browserVersion(resolveVersion()).setup();
        FirefoxOptions options = new FirefoxOptions();
        applyBinary(options, Browser.FIREFOX);
        applyCommon(options);
        applyHeadless(options);
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        return new FirefoxDriver(options);
    }

    private static WebDriver buildRemote(MutableCapabilities caps) {
        String url = CONFIG.remoteUrl();
        Objects.requireNonNull(url, "REMOTE_* требует remoteUrl");
        applyCommon(caps);
        try {
            WebDriver driver = new RemoteWebDriver(new URL(url), caps);
            applyTimeouts(driver);
            return driver;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Неверный remoteUrl: " + url, e);
        }
    }

    private static void applyBinary(MutableCapabilities options, Browser browser) {
        BrowserBinaryResolver.resolve(browser).ifPresent(path -> {
            if (options instanceof ChromeOptions chromeOptions) {
                chromeOptions.setBinary(path);
            } else if (options instanceof EdgeOptions edgeOptions) {
                edgeOptions.setBinary(path);
            } else if (options instanceof FirefoxOptions firefoxOptions) {
                firefoxOptions.setBinary(path);
            }
            Allure.addAttachment("Browser binary", "text/plain", path);
        });
    }

    private static void applyCommon(MutableCapabilities options) {
        if (options instanceof ChromeOptions chromeOptions) {
            chromeOptions.addArguments(
                    "--disable-notifications",
                    "--disable-popup-blocking",
                    "--no-sandbox",
                    "--disable-dev-shm-usage"
            );
        } else if (options instanceof EdgeOptions edgeOptions) {
            edgeOptions.addArguments(
                    "--disable-notifications",
                    "--disable-popup-blocking",
                    "--no-sandbox",
                    "--disable-dev-shm-usage"
            );
        } else if (options instanceof FirefoxOptions firefoxOptions) {
            firefoxOptions.addPreference("dom.webnotifications.enabled", false);
            firefoxOptions.addPreference("privacy.popups.disable_from_events", true);
        }
    }

    private static void applyHeadless(MutableCapabilities options) {
        if (!CONFIG.headless()) {
            return;
        }
        if (options instanceof ChromeOptions chromeOptions) {
            chromeOptions.addArguments("--headless=new", "--window-size=" + CONFIG.browserSize());
        } else if (options instanceof EdgeOptions edgeOptions) {
            edgeOptions.addArguments("--headless=new", "--window-size=" + CONFIG.browserSize());
        } else if (options instanceof FirefoxOptions firefoxOptions) {
            firefoxOptions.addArguments("-headless");
        }
    }

    private static void applyTimeouts(WebDriver driver) {
        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(CONFIG.pageLoadTimeoutSeconds()))
                .scriptTimeout(Duration.ofSeconds(CONFIG.scriptTimeoutSeconds()))
                .implicitlyWait(Duration.ofSeconds(CONFIG.implicitWaitSeconds()));
    }

    private static String resolveVersion() {
        String version = CONFIG.browserVersion();
        return (version == null || version.isBlank()) ? null : version.trim();
    }

    private static void setWindowSize(WebDriver driver) {
        if (CONFIG.headless()) {
            return;
        }
        String size = CONFIG.browserSize();
        if (size == null || size.isBlank()) {
            return;
        }
        try {
            String[] parts = size.split("[x×X]");
            if (parts.length == 2) {
                int width = Integer.parseInt(parts[0].trim());
                int height = Integer.parseInt(parts[1].trim());
                driver.manage().window().setSize(new Dimension(width, height));
                Allure.addAttachment("Размер окна", "text/plain", width + "x" + height);
            }
        } catch (NumberFormatException e) {
            Allure.addAttachment("Некорректный browserSize", "text/plain", size);
        }
    }
}
