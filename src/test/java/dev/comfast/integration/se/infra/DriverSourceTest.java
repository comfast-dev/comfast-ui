package dev.comfast.integration.se.infra;
import dev.comfast.cf.se.infra.DriverSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.SessionId;

import java.util.ArrayList;
import java.util.List;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.se.infra.DriverSource.getDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Disabled("run manually, crash other tests")
class DriverSourceTest {

    @Test void runBrowser() {
        var driver = DriverSource.getDriver();
        assertThat(driver).isNotNull();
    }

    @Test void autoStartDriver() {
        var driver = getDriver();
        driver.quit();
        $("body").getText();
        assertThat(driver).isNotEqualTo(getDriver());
    }

    @SneakyThrows
    @Test
    void autoStartDriverOnMultipleThreads() {
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
    void simultaneouslyReconnectToSameBrowser() {
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
            () -> assertNotEquals(ids[1], getDriver().getSessionId())
        );
    }
}