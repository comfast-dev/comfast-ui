package dev.comfast.cf;
import dev.comfast.cf.common.utils.BrowserContent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FindTest {
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
        shouldFind(2, $("input"));
        shouldFind(2, $("html input"));

        shouldFind(2, $("form input"));
        shouldFind(2, $("form > input"));
        shouldFind(1, $("form >> input"));
        shouldFind(1, $("form").$("input"));
    }

    private void shouldFind(int count, CfLocator locator) {
        assertEquals(count, locator.count());
    }

    @Test void findXpath() {
        shouldFind($("//input"), INPUT_VALUE);
        shouldFind($("//form/input"), INPUT_VALUE);
        shouldFind($("//html//input"), INPUT_VALUE);
        shouldNotFind($("//html/head/input"));
    }

    private void shouldFind(CfLocator locator, String text) {
        assertEquals(text, locator.getValue(), "should find %s in:\n%s".formatted(text, locator));
    }

    private void shouldNotFind(CfLocator locator) {
        assertFalse(locator.exists(), "should not find: " + locator);
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
        assertEquals(2, $("form input").count());
        assertEquals(1, $("form").$("input").count());
        assertEquals(0, $("form").$("article").count());
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
        assertEquals("form", $("option").$(".. >> ..").getTagName());
    }

    @Test
    void findNth() {
        assertEquals("thirdForm", $("form").nth(3).getAttribute("id"));
    }

    @Test
    void count() {
        assertEquals(3, $("form").count());
        assertEquals(2, $("form input").count());
        assertEquals(2, $("//form//input").count());
        assertEquals(1, $("form").$("input").count());
        assertEquals(1, $("form").$(".//input").count());

        assertEquals(0, $("form").$("article").count());
    }

    @Test
    void autoAddDotInNestedXpath() {
        //here, dot is added to second selector
        assertEquals(1, $("//form").$("//input").count());
        //here is standard xpath
        assertEquals(2, $("//form//input").count());
    }
}
