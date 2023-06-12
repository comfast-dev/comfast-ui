package dev.comfast.cf.common.errors;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.selector.SelectorChain;
import dev.comfast.experimental.events.EventListener;
import dev.comfast.experimental.events.model.AfterEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.Command;

import static dev.comfast.cf.CfApi.driverEvents;
import static dev.comfast.cf.CfApi.locatorEvents;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ElementFindFailTest {
    /**
     * EXPERIMENTAL
     * //todo remove it in prod
     */
    @BeforeAll static void initEvents() {
        locatorEvents.addListener("sout", new EventListener<>() {
            @Override public void after(AfterEvent<CfLocator> event) {
                System.out.println(event);
            }
        });
        driverEvents.addListener("sout", new EventListener<>() {
            @Override public void after(AfterEvent<Command> event) {
                System.out.println("-----" + event);
            }
        });
    }

    @Test void properMessageTest() {
        var chain = new SelectorChain("div.active >> //some/xpath >> button:nth-child(2) >> span");
        var cause = new RuntimeException("oh no, failed");
        int index = 2;

        var fail = new ElementFindFail(chain, index, cause);

        assertEquals(
            "Find Element Failed at index: 2 ->\n" +
            "  div.active >> //some/xpath >> button:nth-child(2) >> span\n" +
            "                                ^\n" +
            "                                RuntimeException\n" +
            "\n" +
            "Error details:\n" +
            "java.lang.RuntimeException: oh no, failed\n",
            fail.getMessage());
        assertEquals(index, fail.getFailIndex());
        assertEquals(chain, fail.getSelectorChain());
        assertEquals(cause, fail.getCause());
    }
}