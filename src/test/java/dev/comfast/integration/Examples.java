package dev.comfast.integration;
import dev.comfast.cf.CfApi;
import dev.comfast.cf.CfLocator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.CfApi.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled("Examples depend on external site, not need to run it before every release")
@SuppressWarnings("unused")
class ExamplesTest {
    @Test void example1_chainedLocators() {
        //opens default browser and navigates to url
        open("https://www.wikipedia.org");

        //can mix css and xpath in one locator using separator: ' >> '
        CfLocator englishLink = $(".central-featured >> .//a[.//strong[text()='English']]");

        englishLink.click();

        assertEquals("https://en.wikipedia.org/wiki/Main_Page", CfApi.getDriver().getCurrentUrl());
    }

    @Test void example2_pageObjects() {
        // locators aren't run browser during creation
        // they can be defined before any browser starts, so they fits ideally for page objects
        // Don't need to call any /magic/ function to initialize page objects, just Plain Old Java Objects
        class WikiPage {
            final String url = "https://www.wikipedia.org";
            final CfLocator englishLink = $(".central-featured >> .//a[.//strong[text()='English']]");
            // locators can be chained, so You can reuse their fragments (here, 'wrapper' is reused)
            final CfLocator wrapper = $(".central-featured");
            final CfLocator polskiLink = wrapper.$(".//a[.//strong[text()='Polski']]");
            final CfLocator italianoLink = wrapper.$(".//a[.//strong[text()='Italiano']]");
        }

        var wiki = new WikiPage();

        open(wiki.url);
        wiki.italianoLink.click();

        assertEquals("https://it.wikipedia.org/wiki/Pagina_principale", CfApi.getDriver().getCurrentUrl());
    }

    @Test void example3_autoWaiting() {
        // waiting for element action is automatic, by default set to 4 seconds
        open("https://www.wikipedia.org");
        $("//a[.//strong[text()='English']]").click();
        if($("a.search-toggle").isDisplayed()) $("a.search-toggle").click();
        $("input[name=search]").type("Seleniu");

        //does auto-wait till result appears, and then click on it
        $("//bdi[contains(.,'Selenium (software)')]").click();

        assertEquals("Selenium (software)", $("h1#firstHeading").getText());
        // wait time can be set using environment variable:
        // System.setProperty("cf.timeoutMs", "3000");
        // or in config file: <projectRoot>/appConfig.yaml -> cf.timeoutMs: 5000
    }

    @Test void example4_reuseDriver_1() {
        // You can reuse same browser between multiple runs of application
        // can be useful for debugging when logging in/initialize phase takes some time

        System.setProperty("cf.autoClose", "false");
        System.setProperty("cf.reconnect", "true");

        //some time consuming initialization
        open("https://www.wikipedia.org");
        $("//a[.//strong[text()='English']]").click();
        if($("a.search-toggle").isDisplayed()) $("a.search-toggle").click();
        $("input[name=search]").type("Seleniu");
        $("//bdi[contains(.,'Selenium (software)')]").click();
        assertEquals("Selenium (software)", $("h1#firstHeading").getText());

        //now, after this test the browser is still open and can be reconnected in the next launch
    }

    @Test void example4_reuseDriver_2() {
        // debug, or just continue work with the same browser, don't need to reopen it or log in again
        System.setProperty("cf.autoClose", "false");
        System.setProperty("cf.reconnect", "true");

        //feel free to edit or experiment with the page
        assertEquals("Python[edit]", $("//h5").nth(1).getText());
        assertEquals("Java[edit]", $("//h5").nth(2).getText());
        assertEquals("C#[edit]", $("//h5").nth(3).getText());

        //It can be enabled also in config file: <projectRoot>/appConfig.yaml with content:
        // cf.browser
        //   autoClose: false
        //   reconnect: true
    }

    @Test void example5_dontWorryAboutShadowDom() {
        // in some pages  HTML structure is difficult to use
        // for example if You open eg. page: chrome://settings/downloads
        // You can see this page contains many shadow doms - <html> structures nested in each other
        // in Selenium You need to use JS scripts or many lines of code to access elements inside shadow doms

        // here it's just a matter of adding sub-selector separator ' >> ' as usual
        //see example
        open("chrome://settings/downloads");
        var nestedShadow =
            $("settings-ui >> settings-main >> settings-basic-page >> settings-section[section=downloads] >> " +
              "settings-downloads-page >> #locationLabel");
        assertFalse(nestedShadow.getText().isEmpty());

        //equivalent selector chain
        var sameNestedShadow =
            $("settings-ui").$("settings-main").$("settings-basic-page").$("settings-section[section=downloads]")
                .$("settings-downloads-page").$("#locationLabel");
        assertFalse(sameNestedShadow.getText().isEmpty());
    }

    @Test void example6_meaningfulErrorMessages() {
        //Error messages points to the exact place where the problem occurred
        // see previous example with invalid selector

        try {
            open("chrome://settings/downloads");
            var locatorWithInvalidPart =
                $("settings-ui >> settings-main >> settings-basic-page >> settings-section[section=invalid] >> " +
                  "settings-downloads-page >> #locationLabel");
            locatorWithInvalidPart.getText();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //this code will throw exception with message:
            //Wait failed after 300ms, tried 4 times.Last error:
            //Find Element Failed at index: 3 ->
            //  settings-ui >> settings-main >> settings-basic-page >> settings-section[section=invalid] >>
            //  settings-downloads-page >> #locationLabel
            //                                                         ^
            //                                                         NoSuchElementException
            //
            //Error details:
            //org.openqa.selenium.NoSuchElementException: no such element...

            assertNotNull(e.getMessage());

        }
    }
}
