package dev.comfast.integration;
import dev.comfast.cf.common.utils.BrowserContent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InteractionsTest {
    private static BrowserContent content;

    @BeforeAll
    public static void init() {
        content = new BrowserContent();
    }

    @Test void click() {
        content.setBody("<button onclick='document.querySelector(`input`).value += `a`'>Add letter</button><input>");

        $("button").click();
        assertEquals("a", $("input").getValue());

        $("button").click();
        assertEquals("aa", $("input").getValue());
    }

    @Test void focusAndHover() {
        content.setStyle("button { color: black }" +
                         "button:hover { color: blue }" +
                         "button:focus { color: red }")
            .setBody("<button id='one'>One</button>" +
                     "<button id='two'>Two</button>" +
                     "<button id='three'>Three</button>");

        var one = $("#one");
        var two = $("#two");
        var three = $("#three");

        one.hover();
        two.focus();
        assertAll(
            () -> assertEquals("rgba(0, 0, 255, 1)", one.getCssValue("color")),
            () -> assertEquals("rgba(255, 0, 0, 1)", two.getCssValue("color")),
            () -> assertEquals("rgba(0, 0, 0, 1)", three.getCssValue("color"))
        );
    }

    @Test void dragAndDrop() {
        content.setBody("<input type=range min=0 max=100 value=50><button>Hover to Me</button>");
        var input = $("input");
        assertEquals("50", input.getValue());
        $("input").dragTo($("button"));

        assertEquals("100", input.getValue(), "Drag should change value to 100");
    }

    @Test void type() {
        content.setBody("<input>");
        var input = $("input");
        input.type("abc\n");

        assertEquals("abc", input.getValue());
        //todo more complicated features like .type("{Backspace+ABC}")
    }

    @Test void equalsTest() {
        content.setBody("<button></button>");
        var button = $("button");
        var button2 = $("button");
        assertThat(button).isEqualTo(button2);
    }
}