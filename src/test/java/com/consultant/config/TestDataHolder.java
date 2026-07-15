package com.consultant.config;

import org.aeonbits.owner.ConfigFactory;

public final class TestDataHolder {

    private static final TestData DATA = ConfigFactory.create(TestData.class);

    private TestDataHolder() {
    }

    public static TestData data() {
        return DATA;
    }
}
