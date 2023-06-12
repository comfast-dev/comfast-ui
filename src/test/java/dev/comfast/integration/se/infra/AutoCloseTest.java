package dev.comfast.integration.se.infra;
import dev.comfast.cf.CfApi;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.getDriver;
import static org.assertj.core.api.Assertions.assertThat;

class AutoCloseTest {

    @Disabled("need to be tested manually")
    @Test void autoClose() {
        System.setProperty("cf.browser.autoClose", "true");
        System.setProperty("cf.browser.name", "brave");
        CfApi.open("https://google.com");
        //check manually if browser is closed
        assertThat(getDriver()).isNotNull();
    }
}
