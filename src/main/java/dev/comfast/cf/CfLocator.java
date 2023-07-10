package dev.comfast.cf;
import dev.comfast.cf.common.conditions.Condition;
import dev.comfast.cf.common.errors.ElementFindFail;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static dev.comfast.cf.common.conditions.Conditions.not;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface CfLocator {
    /**
     * get sub-locator related to current one
     *
     * @return Cfloctor
     */
    @SuppressWarnings("java:S100")
    CfLocator $(String selector, Object... selectorParams);
    /**
     * Force find action. If you want only check existence of element use {@link #exists()} method.
     *
     * @return Found element.
     * @throws ElementFindFail if element not found
     */
    CfFoundLocator find();
    /**
     * Force find action, doesn't throw exception.
     * If you want only check existence of element use {@link #exists()} method.
     *
     * @return force locator find action.
     * @throws ElementFindFail if element not found
     */
    Optional<CfFoundLocator> tryFind();
    /**
     * @param cssClass name
     * @return if element have given css class
     */
    boolean hasClass(String cssClass);
    /**
     * @return text of element
     */
    String getText();
    /**
     * @return inner html of element
     */
    String innerHtml();
    /**
     * @return outer html of element
     */
    String outerHtml();
    /**
     * @return value of element
     */
    String getValue();
    /**
     * @return javascript attribute of element
     */
    String getAttribute(String name);
    /**
     * @param name eg. color, opacity, etc.
     * @return css value of given name
     */
    String getCssValue(String name);

    //flags
    /**
     * @return true if element exists in DOM
     */
    boolean exists();
    /**
     * @return html tag name
     */
    String getTagName();
    /**
     * @return true if element is visible, not throw if element not found - just return false
     */
    boolean isDisplayed();

    //mouse
    /**
     * Click on element
     */
    void click();
    /**
     * Tap gesture on element
     */
    void tap();
    /**
     * Focus on element using JS
     */
    void focus();
    /**
     * Hover mouse on element
     */
    void hover();
    /**
     * Drag current element onto other element
     */
    void dragTo(CfLocator target);

    // forms
    /**
     * Generic set value of element. Should work on input, textarea, select, etc.
     *
     * @param value to set
     */
    void setValue(String value);
    /**
     * Check/uncheck checkbox
     *
     * @param state true- check, false - uncheck
     */
    void setChecked(boolean state);
    /**
     * Clear given input/textarea/select/checkbox
     */
    void clear();
    /**
     * Type keys into element
     *
     * @param keys keyboard input, can use special keys in format: {Ctrl}, {Ctrl+Shift+L}, {Shift+some text} etc.
     */
    void type(String keys);
    /**
     * @return number of elements found by locator
     */
    int count();
    /**
     * Executes JavaScript code, where "this" is current element
     *
     * @param script JS Code
     * @param args arguments for JS code
     * @return result of JS code
     */
    Object executeJs(String script, Object... args);
    /**
     * Does mapping against each element found by locator
     *
     * @param func mapping function, eg. locator -> locator.getText()
     * @param <T> mapping result type
     * @return list of mapped values
     */
    <T> List<T> map(Function<CfFoundLocator, T> func);
    /**
     * Iterate thorough all elements matched by locator
     *
     * @param func function, eg. locator -> locator.setValue("abc")
     */
    void forEach(Consumer<CfFoundLocator> func);
    /**
     * nth element found by locator
     *
     * @param nth index starting of 1
     * @return CfLocator
     */
    CfFoundLocator nth(int nth);
    /**
     * Wait for given function return truth value (true, 1, non null object etc.)
     * <pre>{@code
     *  locator.waitFor(el -> el.count() > 0, 3000);
     *  locator.waitFor(el -> el.getText().equals("some text"), 60_000);
     * }</pre>
     *
     * @param timeoutMs timeout in milliseconds
     */
    void waitFor(Function<CfLocator, Object> func, long timeoutMs);
    /**
     * Wait for given condition match
     */
    void should(Condition condition);
    /**
     * Wait fo given condition not match
     */
    default void shouldNot(Condition condition) {
        should(not(condition));
    }
}