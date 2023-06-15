package dev.comfast.cf.se.infra;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverSource {
    private static final DriverManager manager = new DriverManager();

    public static RemoteWebDriver getDriver() {
        return manager.getDriver();
    }
}
