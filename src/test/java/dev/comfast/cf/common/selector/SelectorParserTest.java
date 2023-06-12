package dev.comfast.cf.common.selector;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.common.selector.SelectorParser.isXpath;
import static dev.comfast.cf.common.selector.SelectorParser.normalizeChildSelector;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectorParserTest {
    @Test
    void isXpathTest() {
        assertAll(
            () -> testIsXpath("./body", true),
            () -> testIsXpath(".//body", true),
            () -> testIsXpath("../body", true),
            () -> testIsXpath("..//body", true),
            () -> testIsXpath("//body", true),
            () -> testIsXpath("/body", true),
            () -> testIsXpath("((./div)[1])[2]", true),
            () -> testIsXpath("(.//li)[3]", true),
            () -> testIsXpath("..", true),
            () -> testIsXpath("..//li", true),
            () -> testIsXpath("html div.enabled", false),
            () -> testIsXpath(".some", false)
        );
    }

    private void testIsXpath(String selector, boolean expected) {
        assertEquals(expected, isXpath(selector), "'" + selector + "' -> should be recognized as Xpath");
    }

    @Test
    void normalizeXpath() {
        assertAll(
            () -> assertEquals("./body", normalizeChildSelector("/body")),
            () -> assertEquals(".//body", normalizeChildSelector("//body")),
            () -> assertEquals("../body", normalizeChildSelector("../body")),
            () -> assertEquals("..//body", normalizeChildSelector("..//body"))
        );
    }
}