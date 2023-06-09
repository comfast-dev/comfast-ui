package dev.comfast.cf;
import dev.comfast.cf.common.utils.BrowserContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.util.Utils.withSystemProp;
import static org.assertj.core.api.Assertions.assertThatCode;

public class TimeoutTest {
    static final BrowserContent content = new BrowserContent();

    @BeforeEach
    void addScript() {
        content.clearAll();
        //noinspection JSUnusedLocalSymbols
        content.setScriptTag(
          "function insertElement(parentSelector, elementTag, elementText) {\n" +
          "    const element = document.createElement(elementTag)\n" +
          "    element.append(elementText)\n" +
          "    document.querySelector(parentSelector).append(element)\n" +
          "}\n" +
          "function setTimer(timerElement, timerTimeoutMs, endText, endCallback) {\n" +
          "     const endTime = Date.now() + timerTimeoutMs;\n" +
          "     const interval = setInterval(() => { timerElement.innerText = endTime - Date.now()}, 50)\n" +
          "     setTimeout(() => { clearInterval(interval); timerElement.innerText = endText } , timerTimeoutMs)\n" +
          "     console.log(typeof endCallback)\n" +
          "     if (typeof endCallback === 'function') setTimeout(endCallback, timerTimeoutMs)\n" +
          " }\n"
            );
        content.setBody(
            "<button id='countdown' onclick='" +
            "setTimer(this, 300, `Restart`, () => insertElement(`body`, `p`, `Wait has ended`))" +
            "'>Start countdown</button>"
            );
    }

    @Test void autoWaitShouldWaitBeforeClick() {
        withSystemProp("cf.timeoutMs", () -> {
            System.setProperty("cf.timeoutMs", "600");

            assertThatCode(() -> {
                $("button#countdown").click();
                $("//p[contains(.,'Wait has ended')]").click();
            }).doesNotThrowAnyException();
        });
    }
}
