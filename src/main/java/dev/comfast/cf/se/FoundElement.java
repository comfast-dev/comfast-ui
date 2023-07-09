package dev.comfast.cf.se;
import dev.comfast.cf.CfFoundLocator;
import dev.comfast.cf.common.selector.SelectorChain;
import org.openqa.selenium.WebElement;

public class FoundElement extends SeleniumLocator implements CfFoundLocator {
    private final WebElement foundWebElement;

    public FoundElement(SelectorChain chain, WebElement foundElement) {
        super(chain);
        this.foundWebElement = foundElement;
    }

    @Override protected WebElement doFind() {
        return foundWebElement;
    }
}
