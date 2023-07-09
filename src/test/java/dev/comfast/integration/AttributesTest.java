package dev.comfast.integration;
import dev.comfast.cf.common.utils.BrowserContent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttributesTest {
    @BeforeAll
    public static void init() {
        new BrowserContent().openResourceFile("test.html");
    }

    @Test void textAndHtml() {
        var p = $("#textAndHtml p");
        assertEquals("Hello World !", p.getText(), "default");
        assertEquals("Hello World !", p.getAttribute("innerText"), "innerText");
        assertEquals(" Hello World\n    !", p.getAttribute("textContent"), "textContent");
        assertEquals(" Hello <span>World</span>\n    !", p.innerHtml(), "innerHTML");
        assertEquals("<p> Hello <span>World</span>\n    !</p>", p.outerHtml(), "outerHTML");
    }

    @Test void isDisplayed() {
        assertAll(
            () -> assertTrue($("#isDisplayed .default").exists()),
            () -> assertTrue($("#isDisplayed .default").isDisplayed(), "default"),

            () -> assertTrue($("#isDisplayed .displayNone").exists()),
            () -> assertFalse($("#isDisplayed .displayNone").isDisplayed(), "display:none"),

            () -> assertTrue($("#isDisplayed .visibilityHidden").exists()),
            () -> assertFalse($("#isDisplayed .visibilityHidden").isDisplayed(), "visibility:hidden"),

            () -> assertTrue($("#isDisplayed .opacity01").exists()),
            () -> assertTrue($("#isDisplayed .opacity01").isDisplayed(), "opacity > 0"),

            //playwright returns true, selenium false here
            () -> assertTrue($("#isDisplayed .opacity0").exists()),
            () -> assertFalse($("#isDisplayed .opacity0").isDisplayed(), "opacity == 0")
        );
    }

    @Test void attribute() {
        var input = $(".text input");
        assertAll("HTML / JS Attributes",
            () -> assertEquals("text", input.getAttribute("type")),
            () -> assertEquals("", input.getAttribute("value")),
            () -> assertNull(input.getAttribute("required")),
            () -> assertNull(input.getAttribute("required")),
            () -> assertNull(input.getAttribute("notattribute")),

            // JS attributes
            () -> assertFalse(input.getAttribute("formAction").isEmpty(), "formAction"),
            () -> assertEquals("input", input.getAttribute("localName")),
            () -> assertEquals("inherit", input.getAttribute("contentEditable"))
        );
    }

    @Test void getCssValueTest() {
        assertEquals("rgba(0, 0, 0, 0)", $("h1").getCssValue("background-color"));
    }

//    @Test
//    public void hasAttribute() {
//        content.setBody("<input type='text' value='' required>");
//
//        assertTrue($("input").hasAttribute("type"));
//        assertFalse($("input").hasAttribute("notattribute"));
//    }

    @Test void value() {
        assertEquals("abc", $(".text_abc input").getValue());
        assertNull($(".text_abc label").getValue());
    }
}