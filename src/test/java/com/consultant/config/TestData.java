package com.consultant.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"classpath:testdata.properties", "system:properties", "system:env"})
public interface TestData extends Config {

    @Key("search.query")
    String searchQuery();

    @Key("document.title")
    String documentTitle();

    @Key("article.title")
    String articleTitle();

    @Key("link.text")
    String linkText();

    @Key("link.dst")
    String linkDst();
}
