package dev.comfast.cf.common.selector;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

import static java.lang.String.format;

@RequiredArgsConstructor
public class ChainPointerMessage {
    public static final String ARROW = "^"; // ↑
    private final SelectorChain chain;

    /**
     * Generate command with pointer to selector index, eg. for index = 1:
     * <pre>
     * ul.list >> //li[name='abc'] >> span
     *            ^
     *           Pointer message text
     * </pre>
     */
    public String build(int selectorIndex, String pointerMessage) {
        int selectorOffset = Stream.of(chain.split())
            .limit(selectorIndex)
            .mapToInt(selector -> selector.length() + 4)
            .sum();

        final String spaces = " ".repeat(selectorOffset);
        return format("  %s%n  %s%s%n  %s%s%n",
            chain, spaces, ARROW, spaces, pointerMessage);
    }
}
