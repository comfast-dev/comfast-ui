package dev.comfast.cf.se.infra;
import dev.comfast.cf.CfApi;
import org.junit.jupiter.api.Test;

public class AutoCloseTest {
    @Test void autoClose() {
        System.setProperty("cf.browser.autoClose", "true");
        System.setProperty("cf.browser.name", "brave");
        CfApi.open("https://google.com");
        //check manually if browser is closed
    }
}
