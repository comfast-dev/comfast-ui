package dev.comfast.cf.common.selector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class SelectorChainTest {
    static final String CSS = "some.css";
    static final String XPATH = "//some/xpath";

    @Test
    void createTest() {
        var chain = new SelectorChain(CSS + " >> " + XPATH + " >> " + CSS);
        shouldMatch(chain, CSS, XPATH, CSS);
    }

    private void shouldMatch(SelectorChain chain, String... expectedChainLinks) {
        assertEquals(arrToString(chain.split()), arrToString(expectedChainLinks));
    }

    private String arrToString(String... arr) {
        return "[" + String.join(", ", arr) + "]";
    }

    @Test
    void addTest() {
        var chain1 = new SelectorChain(XPATH);
        var chain2 = chain1.add(CSS).add(CSS);
        var chain3 = chain1.add(CSS).add(CSS);

        assertAll(
            () -> assertNotSame(chain1, chain2),
            () -> assertNotEquals(chain1, chain2),
            () -> shouldMatch(chain2, XPATH, CSS, CSS),
            () -> assertNotSame(chain2, chain3),
            () -> assertEquals(chain2, chain3)
        );
    }
}