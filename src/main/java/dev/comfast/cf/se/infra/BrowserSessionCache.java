package dev.comfast.cf.se.infra;
import dev.comfast.cf.common.errors.CfFrameworkError;
import dev.comfast.util.TempFile;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.stream.Stream;

import static dev.comfast.cf.CfApi.config;
import static dev.comfast.cf.se.infra.SessionLine.parseLines;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.stream;

/**
 * Manage instances of WebDriver
 */
public class BrowserSessionCache {
    private static final TempFile sessionCache = new TempFile("dev.comfast/driverSessionCache.csv");
    private final ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    public RemoteWebDriver getDriver() {
        if(driver.get() != null) return driver.get();
        for(SessionLine session : parseLines(sessionCache.read())) {
            if(session.isFree() && reserveSession(session)) {
                var optionalDriver = session.tryToReconnect();

                if(optionalDriver.isPresent()) {
                    driver.set(optionalDriver.get());
                    return driver.get();
                }
                removeLine(session);
            }
        }

        //not found free driver
        driver.set(doRunDriver());
        addLine(new SessionLine(driver.get()));
        return driver.get();
    }

    private boolean reserveSession(SessionLine line) {
        //todo lock file
        readFile().filter(SessionLine::isFree).forEach(this::removeLine);
        var actualLine = readLine(line, sessionCache);
        if(!actualLine.isFree()) return false;
        updateLine(actualLine.toBuilder()
            .pid(ProcessHandle.current().pid())
            .tid(currentThread().getId())
            .build());
        return true;
    }

    private void updateLine(SessionLine line) {
        //todo lock file
        throw new RuntimeException("not implemented");
    }

    private void addLine(SessionLine line) {
        //todo lock file
        throw new RuntimeException("not implemented");
    }

    private void removeLine(SessionLine line) {
        //todo lock file
        throw new RuntimeException("not implemented");
    }

    private Stream<SessionLine> readFile() {
        return stream(sessionCache.read().trim().split("\n"))
            .map(SessionLine::new);
    }

    private RemoteWebDriver doRunDriver() {
        String name = config.getString("cf.browser.name");
        switch(name) {
            case "brave":
                var opts = new ChromeOptions();
                opts.setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe");
                opts.addArguments("--remote-allow-origins=*");
                return new ChromeDriver(opts);
            case "chrome":
                return new ChromeDriver();
            case "firefox":
                return new FirefoxDriver();
            case "edge":
                return new EdgeDriver();
            default:
                throw new CfFrameworkError("invalid browser name: " + name);
        }
    }
}
