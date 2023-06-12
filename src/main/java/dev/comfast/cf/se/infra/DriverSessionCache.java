package dev.comfast.cf.se.infra;
import dev.comfast.util.TempFile;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.net.URL;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;

/**
 * Caches WebDriver session to avoid creating new session on each test.
 * <p>
 * This is useful for debugging, where creating new session and opening given entry in application is expensive.
 * <p>
 * This class is not thread-safe. Don't use it in parallel tests.
 */
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DriverSessionCache {
    TempFile sessionStoreFile = new TempFile("sessionStoreInfo.txt");
    Supplier<RemoteWebDriver> newDriverSupplier;

    /**
     * Gets WebDriver instance. If session is cached, it will try to reconnect to it.
     */
    public RemoteWebDriver get() {
        return tryToReconnect().orElseGet(() -> {
            log.info("Reconnect failed, creating new WebDriver instance");
            RemoteWebDriver driver = newDriverSupplier.get();
            sessionStoreFile.write(format("%s%n%s",
                driver.getSessionId(),
                ((HttpCommandExecutor) driver.getCommandExecutor()).getAddressOfRemoteServer()));
            return driver;
        });
    }

    private Optional<RemoteWebDriver> tryToReconnect() {
        try {
            log.info("Reconnecting...");
            String[] sessionInfo = sessionStoreFile.read().split("\n");
            RemoteWebDriver driver = fixedSessionDriver(sessionInfo[0], new URL(sessionInfo[1]));
            validateDriverWorks(driver);
            return Optional.of(driver);
        } catch(Throwable e) {
            return Optional.empty();
        }
    }

    private void validateDriverWorks(RemoteWebDriver driver) {
        driver.getTitle();
        driver.getTitle();
    }

    /**
     * Creates new RemoteWebDriver instance with fixed session id and url.
     *
     * @param sessionId session id
     * @param browserAddress address of browser
     * @return WebDriver instance
     */
    private RemoteWebDriver fixedSessionDriver(final String sessionId, URL browserAddress) {
        CommandExecutor executor = new FixedSessionExecutor(browserAddress, new SessionId(sessionId));
        return new RemoteWebDriver(executor, new DesiredCapabilities());
    }
}