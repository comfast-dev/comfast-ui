package dev.comfast.integration;
import dev.comfast.cf.common.utils.BrowserContent;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static org.assertj.core.api.Assertions.assertThat;

class ShadowRootsTest {

    @Test void openShadowDom() {
        new BrowserContent().openResourceFile("test.html");

        assertThat($("my-div >> h3").getText()).isEqualTo("Hello from shadow");
        assertThat($("my-div >> my-div >> h5").getText()).isEqualTo("We need go deeper");
    }
}
