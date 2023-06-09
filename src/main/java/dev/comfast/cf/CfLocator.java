package dev.comfast.cf;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface CfLocator {
    CfLocator $(String selector, Object... selectorParams);
    boolean hasClass(String cssClass);
    String getText();
    String innerHtml();
    String outerHtml();
    String getValue();
    String getAttribute(String name);
    String getCssValue(String name);

    //flags
    boolean exists();
    String getTagName();
    boolean isDisplayed();

    //mouse
    void click();
    void tap();
    void focus();
    void hover();
    void dragTo(CfLocator target);

    // forms
    void setValue(String value);
    void setChecked(boolean state);
    void clear();
    void type(String keys);
    int count();
    Object executeJs(String script, Object... args);
    <T> List<T> map(Function<CfLocator, T> func);
    CfLocator nth(int nth);
}