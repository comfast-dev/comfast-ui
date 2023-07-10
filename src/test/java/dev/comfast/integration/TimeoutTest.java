package dev.comfast.integration;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.utils.BrowserContent;
import dev.comfast.util.time.Stopwatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.CfApi.getDriver;
import static dev.comfast.util.Utils.withSystemProp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeoutTest {
    private final CfLocator start = $("#countdown button[name=start]");
    private final CfLocator timeout = $("#countdown input[name=timeout]");

    @BeforeAll static void openTestPage() {
        new BrowserContent().openResourceFile("test.html");
    }

    @Test void autoWaitForInteractWithElement() {
        withSystemProp("cf.timeoutMs", () -> {
            System.setProperty("cf.timeoutMs", "600");

            //when
            timeout.setValue("300");
            start.click();

            // native selenium will throw exception
            assertThatThrownBy(() -> getDriver().findElement(By.cssSelector("#countdown input[name=timeout]"))
                .sendKeys("abc"))
                .hasMessageContaining("element not interactable");

            // CfLocator will wait
            assertThatCode(() -> timeout.type("def")).doesNotThrowAnyException();
            assertThat(timeout.getValue()).endsWith("def");
        });
    }

    @Test void autoWaitTimeoutFail() {
        withSystemProp("cf.timeoutMs", () -> {
            final long TIMEOUT = 200;
            System.setProperty("cf.timeoutMs", String.valueOf(TIMEOUT));

            //when
            timeout.setValue("500");
            start.click();

            // code will fail after 200ms
            var time = Stopwatch.measure(() -> assertThatThrownBy(
                () -> timeout.type("def"))
                .hasMessageContaining("Wait failed after 200ms"));

            assertThat(time.getMillis()).isGreaterThan(TIMEOUT - 5);
        });
    }
}
