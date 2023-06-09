package dev.comfast.cf.common.errors;
import dev.comfast.cf.common.selector.ChainPointerMessage;
import dev.comfast.cf.common.selector.SelectorChain;
import lombok.Getter;

import static java.lang.String.format;

@Getter
public class ElementFindFail extends RuntimeException {
    private final SelectorChain selectorChain;
    private final int failIndex;

    public ElementFindFail(SelectorChain chain, int failIndex, Throwable cause) {
        super(format("Find Element Failed at index: %d ->\n%s\nError details:\n%s\n",
            failIndex,
            new ChainPointerMessage(chain).build(failIndex, cause.getClass().getSimpleName()),
            cause
        ), cause);
        this.selectorChain = chain;
        this.failIndex = failIndex;
    }

    @Override public String toString() {
        return getMessage();
    }
}
