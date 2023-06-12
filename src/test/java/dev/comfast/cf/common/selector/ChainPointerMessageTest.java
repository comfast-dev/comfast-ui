package dev.comfast.cf.common.selector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChainPointerMessageTest {

    @Test void test0() {
        test(0, "  ul.list >> //li[name='abc'] >> span\n" +
                "  ^\n" +
                "  Point here\n"
        );
    }
    @Test void test1() {
        test(1, "  ul.list >> //li[name='abc'] >> span\n" +
                "             ^\n" +
                "             Point here\n"
        );
    }
    @Test void test2() {
        test(2,"  ul.list >> //li[name='abc'] >> span\n" +
               "                                 ^\n" +
               "                                 Point here\n"
        );
    }

    private void test(int index, String expectedResult) {
        var actual = new ChainPointerMessage(new SelectorChain("ul.list >> //li[name='abc'] >> span"))
            .build(index, "Point here");

        assertEquals(expectedResult, actual);
    }
}