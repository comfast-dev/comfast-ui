package dev.comfast.cf;
import dev.comfast.cf.se.SeleniumLocator;
import dev.comfast.cf.se.infra.DriverSource;
import dev.comfast.experimental.config.ConfigReader;
import dev.comfast.experimental.events.EventsApi;
import dev.comfast.experimental.events.EventsManager;
import dev.comfast.util.waiter.Waiter;
import org.intellij.lang.annotations.Language;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.RemoteWebDriver;

import static dev.comfast.experimental.config.ConfigApi.readConfig;

public class CfApi {
    public static final ConfigReader config = readConfig();

    public static final EventsManager<Command> driverEvents = EventsApi.get("driverEvents", Command.class);
    public static final EventsManager<CfLocator> locatorEvents = EventsApi.get("locatorEvents", CfLocator.class);

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

    public static RemoteWebDriver getDriver() {
        return DriverSource.getDriver();
    }
}
