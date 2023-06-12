package dev.comfast.integration;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.utils.BrowserContent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FindTest {
    private static final String INPUT_VALUE = "some input";
    private static final String OPTION_VALUE = "some option";

    @BeforeAll static void init() {
        new BrowserContent().setBody(format(
            "<form id='inputForm'><input value='%s'/></form>" +
            "<form id='selectForm'><select><option value='%s'>some select</option></select></form>" +
            "<form id='thirdForm'><input/></form>"
            , INPUT_VALUE, OPTION_VALUE));
    }

    @Test void findCss() {
        shouldFindCount($("input"), 2);
        shouldFindCount($("html input"), 2);
        shouldFindCount($("form input"), 2);
        shouldFindCount($("form > input"), 2);
        shouldFindCount($("form >> input"), 1);
        shouldFindCount($("form").$("input"), 1);
    }

    @Test void findXpath() {
        shouldFind($("//input"), INPUT_VALUE);
        shouldFind($("//form/input"), INPUT_VALUE);
        shouldFind($("//html//input"), INPUT_VALUE);
        shouldNotFind($("//html/head/input"));
    }

    @Test void nestedCss() {
        shouldFind($("html").$("form").$("input"), INPUT_VALUE);
    }

    @Test void nestedXpath() {
        shouldFind($("//html").$("//form").$("//input"), INPUT_VALUE);
    }

    @Test void nestedMixedXpathAndCss() {
        shouldFind($("html").$(".//form[1]").$("input"), INPUT_VALUE);
        shouldFind($("html").$(".//form[2]").$("option"), OPTION_VALUE);
        shouldFind($("//body").$("#inputForm").$(".//input"), INPUT_VALUE);
        shouldFind($("//body").$("#selectForm").$(".//option"), OPTION_VALUE);

        shouldFind($("html >> .//form[1] >> input"), INPUT_VALUE);
        shouldFind($("html >> .//form[2] >> option"), OPTION_VALUE);
        shouldFind($("//body >> #inputForm >> .//input"), INPUT_VALUE);
        shouldFind($("//body >> #selectForm >> .//option"), OPTION_VALUE);
    }

    @Test void relativeXpath() {
        shouldFind($("#inputForm").$(".//input"), INPUT_VALUE);
        shouldFind($("#selectForm").$(".//option"), OPTION_VALUE);

        shouldNotFind($("#inputForm").$(".//option"));
        shouldNotFind($("#selectForm").$(".//input"));
    }

    @Test
    void findAll() {
        shouldFindCount($("form input"), 2);
        shouldFindCount($("form").$("input"), 1);
        shouldFindCount($("form").$("article"), 0);
    }

    @Test void crossSearch() {
        assertAll(
            () -> shouldFind($("//form//option"), OPTION_VALUE),
            () -> shouldNotFind($("//form >> .//option")),
            () -> shouldNotFind($("form >> option")),
            () -> shouldNotFind($("form").$("select option"))
        );
    }

    @Test void exists() {
        assertTrue($("//body").$("input").exists());
        assertFalse($("//body").$("article").exists());
    }

    @Test
    void parentXpath() {
        assertEquals("form",
            $("option").$(".. >> ..").getTagName());
    }

    @Test
    void findNth() {
        assertEquals("thirdForm",
            $("form").nth(3).getAttribute("id"));
    }

    @Test void isDisplayedDoesntThrowTest() {
        assertThatCode(() -> {
            assertTrue($("#selectForm >> option").isDisplayed());
            assertFalse($("#selectForm >> lol >> option").isDisplayed()); // 2nd element not found
            assertFalse($("#selectForm >> option >> lol").isDisplayed()); // 3rd element not found
        }).doesNotThrowAnyException();
    }

    @Test
    void count() {
        shouldFindCount($("form"), 3);
        shouldFindCount($("form input"), 2);
        shouldFindCount($("//form//input"), 2);
        shouldFindCount($("form").$("input"), 1);
        shouldFindCount($("form").$(".//input"), 1);
        shouldFindCount($("form").$("article"), 0);
    }

    @Test
    void autoAddDotInNestedXpath() {
        //here, dot is added to second selector
        shouldFindCount($("//form").$("//input"), 1);
        shouldFindCount($("//form//input"), 2);
    }

    private void shouldFind(CfLocator locator, String expectedText) {
        assertEquals(expectedText, locator.getValue(), format("should find '%s' in:\n'%s'", expectedText, locator));
    }

    private void shouldFindCount(CfLocator locator, int expectedCount) {
        assertEquals(locator.count(), expectedCount, format("should find %d matches of locator:\n%s", expectedCount, locator));
    }

    private void shouldNotFind(CfLocator locator) {
        assertFalse(locator.exists(), "should not find: " + locator);
    }

}
