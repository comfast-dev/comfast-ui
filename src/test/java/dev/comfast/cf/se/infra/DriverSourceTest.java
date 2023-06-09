package dev.comfast.cf.se.infra;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.SessionId;

import java.util.ArrayList;
import java.util.List;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.se.infra.DriverSource.getDriver;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
@Disabled("crash other tests, not implemented")
class DriverSourceTest {

    @Test
    public void runBrowser() {
        DriverSource.getDriver();
    }


    @Test
    public void autoStartDriver() {
        $("body").getText();
        getDriver().quit();
    }

    @Test
    public void autoStartClosedDriver() {
        getDriver().quit();
        getDriver().quit();
    }

    @SneakyThrows
    @Test
    @Disabled("crash other tests, not implemented")
    public void autoStartDriverOnMultipleThreads() {
        System.setProperty("cf.browser.reconnect", "false");
        System.setProperty("cf.browser.autoClose", "true");

        List<Thread> threads = new ArrayList<>();
        SessionId[] ids = new SessionId[3];

        for(int i = 0; i < 3; i++) {
            final int finalI = i;
            threads.add(new Thread(() -> {
                var driver = getDriver();
                ids[finalI] = driver.getSessionId();
            }));
        }

        //start all
        threads.forEach(Thread::start);
        //wait for every one end
        for(Thread t : threads) t.join();

        assertAll("Every driver session should be unique",
            () -> assertNotEquals(ids[0], ids[1]),
            () -> assertNotEquals(ids[1], ids[2]),
            () -> assertNotEquals(ids[2], ids[0])
        );

        //test should not brake driver
        $("input").exists();
    }

    @SneakyThrows
    @Test
    @Disabled("not implemented")
    public void simultaneouslyReconnectToSameBrowser() {
        final int count = 2;
        Thread[] threads = new Thread[count];
        SessionId[] ids = new SessionId[count];

        //one driver already run
        var runningId = getDriver().getSessionId();

        for(int i = 0; i < count; i++) {
            final int finalI = i;
            threads[finalI] = new Thread(() -> {
                var driver = getDriver();
                ids[finalI] = driver.getSessionId();
                driver.quit();
            });
        }

        //start all
        for(Thread t : threads) t.start();

        //wait for every thread end
        for(Thread t : threads) t.join();

        assertAll("Every driver session should be unique",
            () -> assertNotEquals(ids[0], ids[1]),
            () -> assertNotEquals(ids[1], getDriver())
        );
    }
}