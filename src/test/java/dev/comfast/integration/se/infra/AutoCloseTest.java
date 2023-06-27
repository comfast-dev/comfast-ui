package dev.comfast.integration.se.infra;
import dev.comfast.cf.CfApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static dev.comfast.cf.CfApi.getDriver;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfSystemProperty(named = "allTests", matches = "true")
class AutoCloseTest {
    @Test void autoClose() {
        System.setProperty("cf.browser.autoClose", "true");
        System.setProperty("cf.browser.name", "brave");
        CfApi.open("https://google.com");
        //check manually if browser is closed
        assertThat(getDriver()).isNotNull();
    }
}
