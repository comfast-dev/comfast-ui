package dev.comfast.cf.se.infra;
import dev.comfast.cf.common.errors.CfFrameworkError;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import static dev.comfast.cf.CfApi.config;

/**
 * One stop point for get WebDriver instances
 * Manages Webdriver lifetime.
 */
@Slf4j
public class DriverSource {
    private static final ThreadLocal<RemoteWebDriver> instances = ThreadLocal.withInitial(
        () -> new DriverSessionCache(DriverSource::runDriver).get()
    );

    /**
     * @return Run driver basing on config key:
     * chrome | fiefox| edge | brave | ...
     */
    private static RemoteWebDriver runDriver() {
        String name = config.getString("cf.browser.name");
        RemoteWebDriver driver = doRunDriver(name);
        if(config.getBool("cf.browser.autoClose")) {
            log.info("Auto closing driver {}", driver.getSessionId());
            Runtime.getRuntime().addShutdownHook(new Thread(driver::quit));
        }
        return driver;
    }

    private static RemoteWebDriver doRunDriver(String name) {
        switch(name) {
            case "brave":
                var opts = new ChromeOptions();
                opts.setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe");
                opts.addArguments("--remote-allow-origins=*");
                return new ChromeDriver(opts);
            case "chrome": return new ChromeDriver();
            case "firefox": return new FirefoxDriver();
            case "edge": return new EdgeDriver();
            default: throw new CfFrameworkError("invalid browser name: " + name);
        }
    }

    public static RemoteWebDriver getDriver() {
        return instances.get();
    }
}
