package dev.comfast.cf.se;
import dev.comfast.cf.CfFoundLocator;
import dev.comfast.cf.common.selector.SelectorChain;
import org.openqa.selenium.WebElement;

import static dev.comfast.util.Utils.trimString;

public class FoundElement extends SeleniumLocator implements CfFoundLocator {
    private final WebElement foundWebElement;

    public FoundElement(SelectorChain chain, WebElement foundElement) {
        super(chain);
        this.foundWebElement = foundElement;
    }

    @Override protected WebElement doFind() {
        return foundWebElement;
    }

    @Override public boolean equals(Object o) {
        return foundWebElement.equals(o);
    }

    @Override public String toString() {
        try {
            return trimString(outerHtml(), 200);
        } catch(Exception ex) {
            return chain + " (not found)";
        }
    }
}
