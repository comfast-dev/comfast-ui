package dev.comfast.cf.common.errors;
import dev.comfast.cf.common.selector.SelectorChain;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElementFindFailTest {
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
        assertEquals(chain, fail.getChain());
        assertEquals(cause, fail.getCause());
    }
}