package dev.comfast.integration;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.utils.BrowserContent;
import dev.comfast.experimental.events.AfterEvent;
import dev.comfast.experimental.events.EventListener;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.Command;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.CfApi.driverEvents;
import static dev.comfast.cf.CfApi.locatorEvents;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttributesTest {
    private static BrowserContent content;

    /**
     * EXPERIMENTAL
     * //todo remove it in prod
     */
    @BeforeAll static void initEvents() {
        locatorEvents.addListener("sout",  new EventListener<>() {
            @Override public void after(AfterEvent<CfLocator> event) {
                System.out.println(event);
            }
        });
        driverEvents.addListener("sout", new EventListener<>() {
            @Override public void after(AfterEvent<Command> event) {
                System.out.println(event);
            }
        });
    }

    @BeforeAll
    public static void init() {
        content = new BrowserContent();
    }

    @Test
    public void textAndHtml() {
        @Language("HTML") final String TEST_HTML = "<p> Hello <span>World \n</span>\n !</p>";

        content.setBody(TEST_HTML);

        var p = $("p");
        assertAll(
            () -> assertEquals("Hello World !", p.getText(), "default"),
            () -> assertEquals("Hello World !", p.getAttribute("innerText"), "innerText"),
            () -> assertEquals(" Hello World \n\n !", p.getAttribute("textContent"), "textContent"),
            () -> assertEquals(" Hello <span>World \n</span>\n !", p.innerHtml(), "innerHTML"),
            () -> assertEquals(TEST_HTML, p.outerHtml(), "outerHTML")
        );
    }

    @Test
    public void isDisplayed() {
        content.setBody(
            "<p id='default'>1</p>" +
            "<p id='displayNone' style='display:none'>2</p>" +
            "<p id='visibilityHidden' style='display:block; visibility:hidden'>3</p>" +
            "<p id='opacity01' style='opacity: 0.1'>4</p>" +
            "<p id='opacity0' style='opacity: 0'>5</p>"
        );

        assertAll(
            () -> assertTrue($("#default").isDisplayed(), "default"),
            () -> assertFalse($("#displayNone").isDisplayed(), "display:none"),
            () -> assertFalse($("#visibilityHidden").isDisplayed(), "visibility:hidden"),
            () -> assertTrue($("#opacity01").isDisplayed(), "opacity > 0")

            // playwright return true, selenium: false
//            () -> assertFalse($("#opacity0").isDisplayed(), "opacity == 0")
        );
    }

    @Test
    public void attribute() {
        content.setBody("<input type='text' value='' required>");

        var input = $("input");
        assertAll("HTML / JS Attributes",
            () -> assertEquals("text", input.getAttribute("type")),
            () -> assertEquals("", input.getAttribute("value")),
            () -> assertEquals("true", input.getAttribute("required")),
            () -> assertNull(input.getAttribute("notattribute")),

            // JS attributes
            () -> assertFalse(input.getAttribute("formAction").isEmpty(), "formAction"),
            () -> assertEquals("input", input.getAttribute("localName")),
            () -> assertEquals("inherit", input.getAttribute("contentEditable"))
        );
    }

    @Test
    public void getCssValueTest() {
        content.setBody("<h1>Hello</h1>");
        assertEquals($("h1").getCssValue("background-color"), "rgba(0, 0, 0, 0)");
    }

//    @Test
//    public void hasAttribute() {
//        content.setBody("<input type='text' value='' required>");
//
//        assertTrue($("input").hasAttribute("type"));
//        assertFalse($("input").hasAttribute("notattribute"));
//    }

    @Test
    public void value() {
        content.setBody("<input type='text' value='abc' required>" +
                        "<span>abc</span>");

        assertEquals("abc", $("input").getValue());

        assertNull($("span").getValue());
        assertEquals("abc", $("span").getText());
    }
}