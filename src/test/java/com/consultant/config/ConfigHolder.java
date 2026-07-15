package com.consultant.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * Единая точка доступа к конфигурации (thread-safe для чтения).
 */
public final class ConfigHolder {

    private static final TestConfig CONFIG = ConfigFactory.create(TestConfig.class);

    private ConfigHolder() {
    }

    public static TestConfig config() {
        return CONFIG;
    }
}
