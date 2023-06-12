package dev.comfast.integration;
import dev.comfast.cf.common.utils.BrowserContent;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ShadowRootsTest {
    private final BrowserContent browserContent = new BrowserContent();

    @Test void openShadowDom() {
        browserContent.openResourceFile("shadow-dom.html");

        var txt = $("//body//article/p[3] >> span").getText();

        assertEquals("Words: 212", txt);
    }
}
