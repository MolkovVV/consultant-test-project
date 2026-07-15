package com.consultant.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "classpath:config.properties",
        "system:properties",
        "system:env"
})
public interface TestConfig extends Config {

    @Key("baseUrl")
    @DefaultValue("https://www.consultant.ru/cons/")
    String baseUrl();

    @Key("browser")
    @DefaultValue("CHROME")
    Browser browser();

    @Key("browserVersion")
    @DefaultValue("")
    String browserVersion();

    @Key("browserSize")
    @DefaultValue("1920x1080")
    String browserSize();

    @Key("browserBinaryPath")
    @DefaultValue("")
    String browserBinaryPath();

    @Key("browserBinaryAutoDetect")
    @DefaultValue("true")
    boolean browserBinaryAutoDetect();

    @Key("headless")
    @DefaultValue("false")
    boolean headless();

    @Key("remoteUrl")
    @DefaultValue("http://localhost:4444/wd/hub")
    String remoteUrl();

    @Key("timeout.seconds")
    @DefaultValue("15")
    int timeoutSeconds();

    @Key("pageLoad.timeout.seconds")
    @DefaultValue("60")
    int pageLoadTimeoutSeconds();

    @Key("script.timeout.seconds")
    @DefaultValue("30")
    int scriptTimeoutSeconds();

    @Key("implicit.wait.seconds")
    @DefaultValue("0")
    int implicitWaitSeconds();

    @Key("retryCount")
    @DefaultValue("0")
    int retryCount();

    @Key("parallel.enabled")
    @DefaultValue("false")
    boolean parallelEnabled();

    @Key("parallel.threads")
    @DefaultValue("2")
    int parallelThreads();
}
