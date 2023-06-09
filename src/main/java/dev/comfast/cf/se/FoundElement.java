package dev.comfast.cf.se;
import dev.comfast.cf.common.selector.SelectorChain;
import org.openqa.selenium.WebElement;

public class FoundElement extends SeleniumLocator {
    private final WebElement foundElement;

    public FoundElement(SelectorChain chain, WebElement foundElement) {
        super(chain);
        this.foundElement = foundElement;
    }

    @Override protected WebElement find() {
        return foundElement;
    }
}
