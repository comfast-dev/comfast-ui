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
    private static BrowserContent content;

    @BeforeAll
    public static void init() {
        content = new BrowserContent();
    }

    @Test void shouldAppearTest() {
        content.setBody(
            "<h1>Hello</h1>"+
            "<h2 style='display: none'>hidden subtitle</h2>"
            );

        assertThatCode(() -> $("h1").should(appear)).doesNotThrowAnyException();
        assertThatCode(() -> $("h2").should(disappear)).doesNotThrowAnyException();

        assertThatThrownBy(() -> $("h1").should(disappear))
            .hasMessageContaining("Condition 'disappear' failed for element: h1");
        assertThatThrownBy(() -> $("h2").should(appear))
            .hasMessageContaining("Condition 'appear' failed for element: h2");
    }
}
