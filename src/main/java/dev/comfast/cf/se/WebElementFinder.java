package dev.comfast.cf.se;
import dev.comfast.cf.common.errors.CfFrameworkError;
import dev.comfast.cf.common.errors.ElementFindFail;
import dev.comfast.cf.common.selector.SelectorChain;
import dev.comfast.cf.common.selector.SelectorParser;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static dev.comfast.cf.se.infra.DriverSource.getDriver;
import static java.util.Arrays.copyOf;

@RequiredArgsConstructor
public class WebElementFinder implements Finder<WebElement> {
    private final SelectorChain chain;

    public WebElement find() {
        return doFind(true, chain.split());
    }

    public List<WebElement> findAll() {
        var selectors = chain.split();
        if(selectors.length == 0) throw new CfFrameworkError("Empty chain, require at least 1 item");

        var lastBy = string2By(selectors[selectors.length - 1]);
        if(selectors.length == 1) return getDriver().findElements(lastBy);

        String[] parents = copyOf(selectors, selectors.length - 1);

        WebElement parent = doFind(false, parents);
        return parent == null ? List.of() : parent.findElements(lastBy);
    }

    private WebElement doFind(boolean throwIfNotFound, String... selectors) {
        int i = 0;
        try {
            SearchContext parent = getDriver();
            for(; i < selectors.length; i++)
                parent = findChild(parent, string2By(selectors[i]));
            return (WebElement) parent;
        } catch(InvalidSelectorException | NoSuchElementException | InvalidArgumentException ex) {
            if(!throwIfNotFound) return null;
            throw new ElementFindFail(chain, i, ex);
        }
    }

    private By string2By(String selector) {
        return SelectorParser.isXpath(selector)
               ? By.xpath(selector)
               : By.cssSelector(selector);
    }

    /**
     * Find child of parent element, includes shadow dow
     */
    private WebElement findChild(SearchContext parent, By by) {
        try {
            return parent.findElement(by);
        } catch(NoSuchElementException ex) {
            //handle case where next element is under shadow DOM
            var shadowRoot = (SearchContext) getDriver().executeScript("return arguments[0].shadowRoot", parent);
            if(shadowRoot != null) return shadowRoot.findElement(by);
            else throw ex;
        }
    }
}
