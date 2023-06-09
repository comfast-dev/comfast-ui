package dev.comfast.cf.se;
import dev.comfast.cf.common.errors.ElementFindFail;
import dev.comfast.cf.common.utils.BrowserContent;
import dev.comfast.util.waiter.WaitTimeout;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.InvalidSelectorException;

import static dev.comfast.cf.CfApi.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorHandlingTest {
    @BeforeAll static void init() {
        new BrowserContent().setBody(
        "<p><button id='foobar' style=\"width: 100px; margin: 40px 0;\">foobar</button></p>\n" +
        "<div style=\"" +
        "   position: absolute;" +
        "   height: 100px;" +
        "   width: 100px;" +
        "   background: rgba(255,0,0,.5);" +
        "   margin-left: 40px;" +
        "   margin-top: -120px;\"></div>" +
        ");\n"
        );
    }

    @Test void properInvalidSelectorError() {
        assertThatThrownBy(() -> $("some.invalid[]]css]").click())
            .hasMessageContaining("Find Element Failed at index: 0")
            .isInstanceOf(WaitTimeout.class)
            .hasCauseInstanceOf(ElementFindFail.class)
            .cause().hasCauseInstanceOf(InvalidSelectorException.class);

        assertThatThrownBy(() -> $("//body >> some.invalid[]]css] >> other").click())
            .hasMessageContaining("Find Element Failed at index: 1")
            .isInstanceOf(WaitTimeout.class)
            .hasCauseInstanceOf(ElementFindFail.class)
            .cause().hasCauseInstanceOf(InvalidSelectorException.class);
    }

    @Test void clickObscuredElement() {
        assertThatThrownBy(() -> $("button#foobar").click())
//            .hasMessageContaining("Element interaction fail") // todo separated exception type
//            .hasCauseInstanceOf(ElementInteractionFail.class);
            .hasMessageContaining("button#foobar")
            .hasMessageContaining("element click intercepted: Element <button id=")
            .hasCauseInstanceOf(ElementClickInterceptedException.class);
    }

    private void expectException(String expectedErrorMessage, Executable func) {
        Throwable err = assertThrows(ElementFindFail.class, func);
        assertThat(err.getMessage()).contains(expectedErrorMessage);
    }
}
