package dev.comfast.cf.common.selector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SelectorParser {
    @SuppressWarnings("RegExpRedundantEscape")
    public static final Pattern IS_XPATH = Pattern.compile("^([\\(\\.]{0,3}\\/|\\.\\.)");

    /**
     * If XPATH passed without dot like "//some/xpath" method will add it like: ".//some/xpath".
     * <p>Explanation: Nested XPATH Selectors should start with dot "./" or ".//", that means "from current node"
     * Otherwise search will is performed from root html element, which isn't expected.</p>
     * @param selector any selector, CSS or XPATH
     * @return same selector, where XPATH is normalized
     */
    public static String normalizeChildSelector(String selector) {
        return isXpath(selector)
               ? selector.replaceFirst("^/", "./")
               : selector;
    }

    /**
     * @param selector string
     * @return true if selector is XPATH
     */
    public static boolean isXpath(String selector) {
        return IS_XPATH.matcher(selector).find();
    }
}
