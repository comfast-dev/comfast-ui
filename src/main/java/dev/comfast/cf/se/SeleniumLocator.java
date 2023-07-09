package dev.comfast.cf.se;
import dev.comfast.cf.CfAbstractLocator;
import dev.comfast.cf.CfFoundLocator;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.conditions.Condition;
import dev.comfast.cf.common.selector.SelectorChain;
import dev.comfast.experimental.events.model.BeforeEvent;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static dev.comfast.cf.CfApi.config;
import static dev.comfast.cf.CfApi.getWaiter;
import static dev.comfast.cf.CfApi.locatorEvents;
import static dev.comfast.cf.se.infra.DriverSource.getDriver;
import static dev.comfast.util.Utils.readResourceFile;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class SeleniumLocator extends CfAbstractLocator implements CfLocator {
    private final Finder<WebElement> finder;

    public SeleniumLocator(String selector, Object... params) {
        this(new SelectorChain(format(selector, params)));
    }

    protected SeleniumLocator(SelectorChain chain) {
        super(chain);
        finder = new WebElementFinder(chain);
    }

    @Override public CfLocator $(String selector, Object... params) {
        return new SeleniumLocator(chain.add(format(selector, params)));
    }

    @Override public CfFoundLocator find() {
        return new FoundElement(chain, doFind());
    }

    @Override public Optional<CfFoundLocator> tryFind() {
        try {
            return Optional.of(find());
        } catch(Exception e) {return Optional.empty();}
    }

    @Override public String getAttribute(String name) {
        return action(() -> doFind().getAttribute(name), "getAttribute", name);
    }

    @Override public String getCssValue(String name) {
        return action(() -> doFind().getCssValue(name), "getCssValue", name);
    }

    @Override public String getTagName() {
        return action(() -> doFind().getTagName(), "getTagName");
    }

    @Override public boolean exists() {
        return action(() -> tryFind().isPresent(), "isDisplayed");
    }

    @Override public boolean isDisplayed() {
        return action(() -> doTryFind().map(WebElement::isDisplayed).orElse(false), "isDisplayed");
    }

    @Override public void tap() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override public void click() {
        action(() -> doFind().click(), "click");
    }

    @Override public void focus() {
        action(() -> doExecuteJs("el.focus()"), "focus");
    }

    @Override public void hover() {
        action(() -> new Actions(getDriver()).moveToElement(doFind()).perform(), "hover");
    }

    /**
     * Uses javaScript implementation. Works faster and more reliable
     */
    @Override public void dragTo(CfLocator target) {
        action(() -> {
            WebElement targetEl = ((SeleniumLocator) target).doFind();
            doExecuteJs(readResourceFile("js/dragAndDrop.js") +
                "executeDragAndDrop(el, arguments[0])",
                targetEl);

// Selenium native implementation (causes problems)
// new Actions(getDriver()).dragAndDrop(find(), targetEl).perform();
        }, "dragTo", target);
    }

    @Override public void setValue(String value) {
        action(() -> doExecuteJs("el.value = arguments[0]", value), "setValue:", value);
    }

    @Override public void setChecked(boolean state) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override public void clear() {
        action(() -> doFind().clear(), "clear");
    }

    @Override public void type(String keys) {
        action(() -> doFind().sendKeys(keys), "type:", keys);
    }

    @Override public int count() {
        return action(() -> finder.findAll().size(), "count");
    }

    @Override public CfFoundLocator nth(int nth) {
        return action(() -> new FoundElement(chain, finder.findAll().get(nth - 1)),
            "nth(%d)", nth);
    }

    @Override public void waitFor(Function<CfLocator, Object> func, long timeoutMs) {
        action(timeoutMs, (Runnable) func, "wait for function");
    }

    @Override public void should(Condition condition) {
        var waiter = getWaiter().configure(c ->
            c.timeoutMs(config.getLong("cf.timeoutMs"))
                .description("should " + condition));

        locatorEvents.action(
            new BeforeEvent<>(this, "should " + condition),
            () -> waiter.waitFor(() -> condition.expect(this)));
    }

    @Override public Object executeJs(String script, Object... args) {
        return action(() -> doExecuteJs(script, args), "ececuteJs", script, args);
    }

    @Override public <T> List<T> map(Function<CfFoundLocator, T> func) {
        return action(() -> finder.findAll().stream()
            .map(el -> new FoundElement(chain, el))
            .map(func).collect(toList()), "map");
    }

    @Override public void forEach(Consumer<CfFoundLocator> func) {
        action(() -> finder.findAll().stream()
            .map(el -> new FoundElement(chain, el))
            .forEach(func), "forEach");
    }

    /**
     * Execute JS, where variable: el ===> current element
     */
    protected Object doExecuteJs(String script, Object... args) {
        var argsWithWebElement = addArgument(args, doFind());
        //noinspection JSUnusedLocalSymbols
        return getDriver().executeScript(
            "const el = arguments[" + args.length + "];\n" + script, argsWithWebElement);
    }

    protected WebElement doFind() {
        //find event
        return finder.find();
    }

    protected Optional<WebElement> doTryFind() {
        try {
            return Optional.of(doFind());
        } catch(Exception e) {return Optional.empty();}
    }

    /**
     * @return new array with added element at the end
     */
    private Object[] addArgument(Object[] array, Object addElement) {
        Object[] newArray = new Object[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = addElement;
        return newArray;
    }
}
