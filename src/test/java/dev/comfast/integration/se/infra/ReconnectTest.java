package dev.comfast.integration.se.infra;
import dev.comfast.cf.se.infra.DriverSessionCache;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("run manually")
class ReconnectTest {
    @Test
    void reconnectTest() {
        RemoteWebDriver driver1 = new DriverSessionCache(ChromeDriver::new).get();
        //now stop program and run again
        RemoteWebDriver driver2 = new DriverSessionCache(ChromeDriver::new).get();

        assertEquals(driver1.getSessionId(), driver2.getSessionId());
    }
}