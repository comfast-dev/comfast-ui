package dev.comfast.cf.common.selector;
import lombok.EqualsAndHashCode;

import static dev.comfast.cf.common.selector.SelectorParser.normalizeChildSelector;

/**
 * Represents chain of CSS / XPath selectors
 */
@EqualsAndHashCode
public class SelectorChain {
    private static final String SEPARATOR = " >> ";
    private final String chain;

    public SelectorChain(String selector) {
        this.chain = selector;
    }

    public SelectorChain add(String selector) {
        return new SelectorChain(chain + SEPARATOR + normalizeChildSelector(selector));
    }

    public String[] split() {
        return chain.split(SEPARATOR);
    }

    @Override
    public String toString() {
        return chain;
    }
}
