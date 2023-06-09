package dev.comfast.cf;
import dev.comfast.cf.se.SeleniumLocator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.util.Utils.withSystemProp;
import static java.lang.System.clearProperty;
import static java.lang.System.setProperty;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CfApiTest {
    @Test
    @Disabled("playwright is currently suspended")
    void returnCorrectImplementation() {
        withSystemProp("cf.backend", () -> {
            assertAll(
                () -> {
                    clearProperty("cf.backend");
                    assertEquals($("abc").getClass(), SeleniumLocator.class);
                },
                () -> {
                    setProperty("cf.backend", "SELENIUM");
                    assertEquals($("abc").getClass(), SeleniumLocator.class);
                },
//                () -> {
//                    setProperty("cf.backend", "PLAYWRIGHT");
//                    assertEquals($("abc").getClass(), CfPlaywrightLocator.class);
//                },
                () -> {
                    setProperty("cf.backend", "lol");
                    assertThrows(RuntimeException.class, () -> $("abc"));
                }
            );
        });
    }
}