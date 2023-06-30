package dev.comfast.integration;
import dev.comfast.cf.common.utils.BrowserContent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.common.conditions.Conditions.appear;
import static dev.comfast.cf.common.conditions.Conditions.disappear;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShouldTest {
    @BeforeAll static void init() {
        new BrowserContent().openResourceFile("test.html");
    }

    @Test void shouldAppearTest() {
        var defaultElement = $("#default");
        var displayNoneElement = $("#displayNone");

        assertThatCode(() -> defaultElement.should(appear)).doesNotThrowAnyException();
        assertThatCode(() -> displayNoneElement.should(disappear)).doesNotThrowAnyException();

        assertThatThrownBy(() -> defaultElement.should(disappear))
            .hasMessageContaining("Condition 'disappear' failed for element:");
        assertThatThrownBy(() -> displayNoneElement.should(appear))
            .hasMessageContaining("Condition 'appear' failed for element:");
    }
}
