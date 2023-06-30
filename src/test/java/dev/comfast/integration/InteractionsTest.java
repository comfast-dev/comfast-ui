package dev.comfast.integration;
import dev.comfast.cf.common.utils.BrowserContent;
import dev.comfast.cf.common.utils.Tracer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.CfApi.driverEvents;
import static dev.comfast.cf.CfApi.getDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InteractionsTest {
    @BeforeAll
    public static void init() {
        new BrowserContent().openResourceFile("test.html");
    }

    @Test void click() {
        var btn = $("#clicker button");
        var count = $("#clicker span");

        assertThat(count.getText()).isEqualTo("0");

        btn.click();
        btn.click();
        btn.click();
        assertThat(count.getText()).isEqualTo("3");
    }

    @Test void focusAndHover() {
        var one = $("#focusAndHover #one");
        var two = $("#focusAndHover #two");
        var three = $("#focusAndHover #three");

        one.hover();
        two.focus();
        assertAll(
            () -> assertEquals("rgba(0, 0, 255, 1)", one.getCssValue("color")),
            () -> assertEquals("rgba(255, 0, 0, 1)", two.getCssValue("color")),
            () -> assertEquals("rgba(0, 0, 0, 1)", three.getCssValue("color"))
        );
    }

    @Test void dragAndDrop() {
        var dropZone1 = $("#dragAndDrop div").nth(1);
        var dropzone2 = $("#dragAndDrop div").nth(2);
        var dragMe = $("#dragMe");
        var currentDropZone = $("#dragMe >> ..");

        dragMe.dragTo(dropzone2);
        assertThat(currentDropZone.getText()).contains("drop zone 2");

        dragMe.dragTo(dropZone1);
        assertThat(currentDropZone.getText()).contains("drop zone 1");
    }

    @Disabled("Native WebDriver Actions Drag&Drop does not work fine")
    @Test void webdriverDragAndDrop() {
        //assign tracer
        driverEvents.addListener("tracer", new Tracer());
        var div = getDriver().findElement(By.cssSelector("#dragAndDrop div"));
        var dragMe = getDriver().findElement(By.cssSelector("#dragAndDrop #dragMe"));
        var dragMeParent = getDriver().findElement(By.xpath("//*[@id='dragMe']/.."));

        new Actions(getDriver())
            .dragAndDrop(dragMe, div)
            .perform();

        assertThat(dragMeParent.getAttribute("id")).startsWith("div");

        driverEvents.removeListener("tracer");
    }

    @Test void type() {
        final String TEXT = "xyz";
        var el = $("input");

        el.clear();
        el.type(TEXT + "\n");

        assertEquals(TEXT, el.getValue());
        //todo more complicated features like .type("{Backspace+ABC}")
    }

    @Test void equalsTest() {
        var button1 = $("button");
        var button2 = $("button");
        var input = $("input");
        assertThat(button1).isEqualTo(button2);
        assertThat(button1).isNotEqualTo(input);
    }
}