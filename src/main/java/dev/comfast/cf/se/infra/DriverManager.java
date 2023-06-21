package dev.comfast.cf.se.infra;
import dev.comfast.cf.common.errors.CfFrameworkError;
import dev.comfast.util.TempFile;
import lombok.SneakyThrows;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.net.URI;
import java.nio.file.Files;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static dev.comfast.cf.CfApi.config;
import static java.lang.Thread.currentThread;
/**
 * Manage instances of WebDriver
 */
public class DriverManager {
    private static final TempFile sessionCache = new TempFile("dev.comfast/driverSessionCache.csv");
    /**
     * Comes from:
     * <li> previous runs (thorough cache file)
     * <li> ended threads.
     */
    private final Deque<RemoteWebDriver> freeDrivers = new LinkedList<>();
    private final Map<Thread, RemoteWebDriver> usedDrivers = new ConcurrentHashMap<>();

    public DriverManager() {
        initialize();
    }

    public RemoteWebDriver getDriver() {
        //todo ReentrantReadWriteLock for file
        // https://medium.com/analytics-vidhya/advanced-locking-in-java-reentrant-read-write-lock-b40fce0833de
        // https://www.geeksforgeeks.org/reentrantreadwritelock-class-in-java/

        if(usedDrivers.get(currentThread()) == null) {
//            updateFreeDrivers();
            if(freeDrivers.isEmpty()) {
                var driver = doRunDriver();
                System.out.println("-----start new driver: " + driver.getSessionId());
                freeDrivers.push(driver);
                if(config.getBool("cf.browser.reconnect")) appendSessionFile(driver);
            }
            System.out.println("-------put new driver: " + currentThread());
            System.out.println(this);
            usedDrivers.put(currentThread(), freeDrivers.pop());
        }

        return usedDrivers.get(currentThread());
    }

    private void appendSessionFile(RemoteWebDriver driver) {
        String sessionLine = ((HttpCommandExecutor) driver.getCommandExecutor()).getAddressOfRemoteServer() + "#" + driver.getSessionId();

        sessionCache.write(sessionCache.read() + sessionLine + "\n");
    }

    /**
     * Remove dead threads, remove dead drivers.
     */
    private void updateFreeDrivers() {
        //remove end threads
        usedDrivers.keySet().forEach(t -> {
            if(!t.isAlive()) {
                System.out.println("-----REMOVING THREAD: " + t);
                var driver = usedDrivers.remove(t);
                freeDrivers.add(driver);
            }
        });
    }

    @SneakyThrows
    private synchronized void initialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(config.getBool("cf.browser.autoClose")) quitAll();
        }));

        if(!config.getBool("cf.browser.reconnect") || !Files.exists(sessionCache.file)) return;

        var newContent = new StringBuilder();

        String content = sessionCache.read().trim();
        if(content.isEmpty()) return;
        for(var session : content.split("\n")) {
            var driver = fixedSessionDriver(session);
            if(checkDriver(driver)) {
                newContent.append(session).append("\n");
                if(!usedDrivers.containsValue(driver)) {
                    freeDrivers.add(driver);
                }
            }
        }

        sessionCache.write(newContent.toString());
    }

    /**
     * Quit all drivers
     */
    private void quitAll() {
        Stream.concat(usedDrivers.values().stream(), freeDrivers.stream())
            .forEach(driver -> {
                try {driver.quit();} catch(Exception e) {
                    //ignore
                }
            });
    }

    /**
     * Creates new RemoteWebDriver instance with fixed session id and url.
     *
     * @param sessionString in format {url}#{sessionId}
     * @return RemoteWebDriver instance
     */
    @SneakyThrows
    private RemoteWebDriver fixedSessionDriver(String sessionString) {
        String[] parts = sessionString.split("#");

        CommandExecutor executor = new FixedSessionExecutor(new URI(parts[0]).toURL(), new SessionId(parts[1]));
        return new RemoteWebDriver(executor, new DesiredCapabilities());
    }

    private boolean checkDriver(RemoteWebDriver driver) {
        try {
            var res = driver.getCommandExecutor().execute(
                new Command(driver.getSessionId(), "getTitle"));
            return res.getStatus() == 0;
        } catch(Exception e) {
            return false;
        }
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
