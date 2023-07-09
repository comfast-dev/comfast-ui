package dev.comfast.integration;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.conditions.Condition;
import dev.comfast.cf.common.utils.BrowserContent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.common.conditions.Conditions.appear;
import static dev.comfast.cf.common.conditions.Conditions.disappear;
import static dev.comfast.cf.common.conditions.Conditions.exists;
import static dev.comfast.cf.common.conditions.Conditions.hasAttribute;
import static dev.comfast.cf.common.conditions.Conditions.not;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShouldTest {
    @BeforeAll static void init() {
        new BrowserContent().openResourceFile("test.html");
    }

    final CfLocator appearedElement = $("#default");
    final CfLocator displayNoneElement = $("#displayNone");
    final CfLocator notExistElement = $("#notExistId");
    final CfLocator input = $("#testForm .text input");
    final CfLocator disabledInput = $("input[disabled]");
    final CfLocator inputWithValue = $("input[value='abc']");

    @Test void shouldExistsTest() {
        shouldPass(appearedElement, exists);
        shouldPass(displayNoneElement, exists);
        shouldPass(notExistElement, not(exists));

        shouldFail(appearedElement, not(exists), "Condition 'not exists' failed for element: #default");
        shouldFail(notExistElement, exists, "Condition 'exists' failed. Element not found");
    }

    @Test void shouldAppearTest() {
        shouldPass(appearedElement, appear);
        shouldPass(appearedElement, not(disappear));
        shouldPass(displayNoneElement, disappear);
        shouldPass(displayNoneElement, not(appear));
        shouldPass(notExistElement, not(appear));
        shouldPass(notExistElement, disappear);

        shouldFail(appearedElement, disappear, "Condition 'disappear' failed for element:");
        shouldFail(displayNoneElement, not(disappear), "Condition 'not disappear' failed for element:");
        shouldFail(displayNoneElement, appear, "Condition 'appear' failed for element:");
        shouldFail(notExistElement, appear, "Condition 'appear' failed. Element not found:");
        shouldFail(appearedElement, not(appear), "Condition 'not appear' failed for element:");
    }

    @Test void multipleNotTest() {
        shouldFail(appearedElement, not(not(not(appear))), "Condition 'not appear' failed for element:");
        shouldFail(appearedElement, not(not(not(not(appear)))), "Condition 'appear' failed for element:");
    }

    @Test void shouldHasAttributeTest() {
        shouldPass(disabledInput, hasAttribute("disabled"));
        shouldFail(disabledInput, not(hasAttribute("disabled")),
            "Condition: not has attribute: 'disabled' failed for element: <input disabled=\"\">");

        shouldPass(input, not(hasAttribute("value", "abc")));
        shouldFail(input, hasAttribute("value", "abc"),
            "Condition: has attribute value='abc' failed for element: <input type=\"text\" value=\"\">");

        shouldPass(inputWithValue, hasAttribute("value", "abc"));
        shouldFail(input, hasAttribute("value", "def"),
            "Condition: has attribute value='def' failed for element:");
    }

    @Test void shouldHasTextTest() {

    }

    @Test void shouldHasCssClassTest() {
    }

    @Test void shouldHaveCountTest() {
    }

    private void shouldPass(CfLocator target, Condition condition) {
        assertThatCode(() -> target.should(condition))
            .doesNotThrowAnyException();
    }

    private void shouldFail(CfLocator target, Condition condition, String errorMessage) {
        assertThatThrownBy(() -> target.should(condition))
            .hasMessageContaining(errorMessage);
    }
}
