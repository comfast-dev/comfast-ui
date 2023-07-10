package dev.comfast.cf.common.errors;
import dev.comfast.cf.common.selector.ChainPointerMessage;
import dev.comfast.cf.common.selector.SelectorChain;
import lombok.Getter;

import static java.lang.String.format;

/**
 * Indicates that element was not found at given index in selector chain.
 * <p>Reason is stored in cause Throwable.</p>
 */
@Getter
public class ElementFindFail extends RuntimeException {
    private final SelectorChain chain;
    private final int failIndex;

    public ElementFindFail(SelectorChain chain, int failIndex, Throwable cause) {
        super(cause);
        this.chain = chain;
        this.failIndex = failIndex;
    }

    @Override public String toString() {
        return getMessage();
    }

    @Override public String getMessage() {
        return format("Find Element Failed at index: %d ->\n%s\nError details:\n%s\n",
            failIndex, getPointer(), getCause()
        );
    }

    /**
     * Returns pointer to element in selector chain. e.g.
     * <pre>
     *   #notExistId >> .myClass
     *                  ^
     *                  NoSuchElementException
     * </pre>
     */
    public String getPointer() {
        return new ChainPointerMessage(chain).build(failIndex, getCause().getClass().getSimpleName());
    }
}
