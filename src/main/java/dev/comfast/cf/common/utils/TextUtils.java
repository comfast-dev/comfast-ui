package dev.comfast.cf.common.utils;
public class TextUtils {
    /**
     * Replace all whitespaces/newlines with single space
     */
    public static String flatText(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }
}
