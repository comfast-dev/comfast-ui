package dev.comfast.cf;
import dev.comfast.cf.common.events.EventsManager;
import dev.comfast.cf.se.SeleniumLocator;
import dev.comfast.config.ConfigReader;
import dev.comfast.util.waiter.Waiter;
import org.intellij.lang.annotations.Language;

import static dev.comfast.cf.se.infra.DriverSource.getDriver;

public class CfApi {
    public static final ConfigReader config = new ConfigReader();
    public static final EventsManager<CfLocator> locatorEvents = new EventsManager<>();

    /**
     * Open given url, run browser when needed.
     */
    public static void open(String url) {
        getDriver().navigate().to(url);
    }

    /**
     * @param selector CSS or XPATH selector
     * @param params parameters used in selector (placeholders: %s, %d)
     */
    public static CfLocator $(String selector, Object... params) {
        return new SeleniumLocator(selector, params);
    }

    public static Object executeJs(@Language("JavaScript") String script, Object... args) {
        return getDriver().executeScript(script, args);
    }

    public static Waiter getWaiter() {
        return new Waiter(config.getLong("cf.timeoutMs"));
    }
}
