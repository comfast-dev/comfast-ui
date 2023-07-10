package dev.comfast.cf;
import dev.comfast.cf.common.selector.SelectorChain;
import dev.comfast.experimental.events.model.BeforeEvent;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

import static dev.comfast.cf.CfApi.getWaiter;
import static dev.comfast.cf.CfApi.locatorEvents;
import static java.util.Arrays.asList;

@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class CfAbstractLocator implements CfLocator {
    protected final SelectorChain chain;

    public boolean hasClass(String cssClass) {
        return asList(getAttribute("class").split(" ")).contains(cssClass);
    }

    public String getText() {
        return getAttribute("innerText");
    }

    public String innerHtml() {
        return getAttribute("innerHTML");
    }

    public String outerHtml() {
        return getAttribute("outerHTML");
    }

    public String getValue() {
        return getAttribute("value");
    }

    @Override public String toString() {
        return chain.toString();
    }

    /**
     * Does an action, wraps it into:
     * - Event: can set Before and After listeners to it
     * - Waiter - action will be repeated in case of fail
     * <code>
     * var someText = action(() -> grabSomeText(1), "grab some text", 1)
     * </code>
     *
     * @param actionFunction actionFunction
     * @param name our name of action, e.g. method name
     * @param params action params, e.g. method params
     * @return actionFunction result
     */
    protected <T> T action(Supplier<T> actionFunction, String name, Object... params) {
        return locatorEvents.action(
            new BeforeEvent<>(this, name, params),
            () -> getWaiter().waitFor(actionFunction));
    }

    /**
     * go to {@link #action(Supplier, String, Object...)}
     */
    protected void action(Runnable actionFunction, String name, Object... params) {
        locatorEvents.action(
            new BeforeEvent<>(this, name, params),
            () -> getWaiter().waitFor(actionFunction));
    }

    /**
     * go to {@link #action(Supplier, String, Object...)}
     */
    protected void action(long timeoutMs, Runnable actionFunction, String name, Object... params) {
        locatorEvents.action(
            new BeforeEvent<>(this, name, params),
            () -> getWaiter().configure(b -> b.timeoutMs(timeoutMs))
                .waitFor(actionFunction));
    }
}
