package dev.comfast.integration.se;
import dev.comfast.cf.common.errors.ElementFindFail;
import dev.comfast.cf.common.utils.BrowserContent;
import dev.comfast.util.waiter.WaitTimeout;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.InvalidSelectorException;

import static dev.comfast.cf.CfApi.$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ErrorHandlingTest {
    @BeforeAll static void init() {
        new BrowserContent().setBody(
        "<p><button id='obscuredButton' style=\"width: 100px; margin: 40px 0;\">foobar</button></p>\n" +
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
        var invalidCss = $("some.invalid[]]css]");
        assertThatThrownBy(invalidCss::click)
            .isInstanceOf(WaitTimeout.class)
            .hasCauseInstanceOf(ElementFindFail.class)
            .cause()
            .hasMessageContaining("Find Element Failed at index: 0")
            .hasCauseInstanceOf(InvalidSelectorException.class);

        var invalidCss2 = $("//body >> some.invalid[]]css] >> other");
        assertThatThrownBy(invalidCss2::click)
            .isInstanceOf(WaitTimeout.class)
            .hasCauseInstanceOf(ElementFindFail.class)
            .cause()
            .hasMessageContaining("Find Element Failed at index: 1")
            .hasCauseInstanceOf(InvalidSelectorException.class);
    }

    @Test void clickObscuredElement() {
        assertThatThrownBy(() -> $("#obscuredButton").click())
//            .hasMessageContaining("Element interaction fail") // todo separated exception type
//            .hasCauseInstanceOf(ElementInteractionFail.class);
            .hasCauseInstanceOf(ElementClickInterceptedException.class)
            .cause()
            .hasMessageContaining("#obscuredButton")
            .hasMessageContaining("element click intercepted: Element <button id=");
    }
}
