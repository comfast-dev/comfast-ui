package dev.comfast.cf.se;
import dev.comfast.cf.CfAbstractLocator;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.selector.SelectorChain;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static dev.comfast.cf.se.infra.DriverSource.getDriver;
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

    @Override public String getAttribute(String name) {
        return action(() -> find().getAttribute(name), "getAttribute", name);
    }

    @Override public String getCssValue(String name) {
        return action(() -> find().getCssValue(name), "getCssValue", name);
    }

    @Override public String getTagName() {
        return action(() -> find().getTagName(), "getTagName");
    }

    @Override public boolean exists() {
        return action(() -> tryFind().isPresent(), "isDisplayed");
    }

    @Override public boolean isDisplayed() {
        return action(() -> tryFind().map(WebElement::isDisplayed).orElse(false), "isDisplayed");
    }

    @Override public void tap() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override public void click() {
        action(() -> find().click(), "click");
    }

    @Override public void focus() {
        action(() -> doExecuteJs("el.focus()"), "focus");
    }

    @Override public void hover() {
        var target = find();
        action(() -> new Actions(getDriver()).moveToElement(target).perform(), "hover");
    }

    @Override public void dragTo(CfLocator target) {
        action(() -> {
            WebElement fromEl = find();
            WebElement targetEl = ((SeleniumLocator) target).find();

            new Actions(getDriver()).clickAndHold(fromEl).release(targetEl).perform();
        }, "dragTo", target);
    }

    @Override public void setValue(String value) {
        action(() -> doExecuteJs("el.setValue(arguments[0])", value), "setValue:", value);
    }

    @Override public void setChecked(boolean state) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override public void clear() {
        action(() -> find().clear(), "clear");
    }

    @Override public void type(String keys) {
        action(() -> find().sendKeys(keys), "type:", keys);
    }

    @Override public int count() {
        return action(() -> finder.findAll().size(), "count");
    }

    @Override public CfLocator nth(int nth) {
        return action(() -> new FoundElement(chain, finder.findAll().get(nth - 1)),
            "nth(%d)", nth);
    }

    @Override public Object executeJs(String script, Object... args) {
        return action(() -> doExecuteJs(script, args), "ececuteJs", script, args);
    }

    @Override public <T> List<T> map(Function<CfLocator, T> func) {
        return action(() ->
            finder.findAll().stream()
                .map(el -> new FoundElement(chain, el))
                .map(func).collect(toList()), "map");
    }

    protected Object doExecuteJs(String script, Object... args) {
        WebElement webElement = find();

//        allArgs is === [...args, webElement]
        Object[] allArgs = new Object[args.length + 1];
        System.arraycopy(args, 0, allArgs, 0, args.length);
        allArgs[args.length] = webElement;

        return getDriver().executeScript("const el = arguments[" + args.length + "];\n" + script, allArgs);
    }

    protected WebElement find() {
        //find event
        return finder.find();
    }

    protected Optional<WebElement> tryFind() {
        try { return Optional.of(find());
        } catch(Exception e) { return Optional.empty(); }
    }
}
