package dev.comfast.cf.common.selector;
import java.util.regex.Pattern;

public class SelectorParser {
    @SuppressWarnings("RegExpRedundantEscape")
    public static Pattern IS_XPATH = Pattern.compile("^([\\(\\.]{0,3}\\/|\\.\\.)");

    public static String normalizeChildSelector(String selector) {
        return isXpath(selector)
               ? selector.replaceFirst("^/", "./")
               : selector;
    }

    public static boolean isXpath(String selector) {
        return IS_XPATH.matcher(selector).find();
    }
}
