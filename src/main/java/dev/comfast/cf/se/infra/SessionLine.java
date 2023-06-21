package dev.comfast.cf.se.infra;
import lombok.Builder;
import lombok.SneakyThrows;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import static dev.comfast.rgx.RgxApi.rgx;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;

@Builder(toBuilder = true)
class SessionLine {
    final long pid;
    final long tid;
    final String url;
    final String sessionId;

    public SessionLine(String text) {
        var match = rgx("todo").match(text).throwIfEmpty("Invalid SessionLine text");
        pid = parseLong(match.get(1));
        tid = parseLong(match.get(2));
        url = match.get(3);
        sessionId = match.get(4);
    }

    public SessionLine(RemoteWebDriver driver) {
        this.pid = ProcessHandle.current().pid();
        this.tid = currentThread().getId();
        this.url = ((HttpCommandExecutor) driver.getCommandExecutor()).getAddressOfRemoteServer().toString();
        this.sessionId = driver.getSessionId().toString();
    }

    public static SessionLine[] parseLines(String content) {
        return Arrays.stream(content.trim().split("\n"))
                      .map(SessionLine::new)
                      .toArray(SessionLine[]::new);
    }

    boolean isFree() {
        return status.equals("free");
    }

    @SneakyThrows
    Optional<RemoteWebDriver> tryToReconnect() {
        var executor = new FixedSessionExecutor(
            new URI(url).toURL(),
            new SessionId(sessionId)
        );
        return testExecutor(executor)
               ? Optional.of(new RemoteWebDriver(executor, new DesiredCapabilities()))
               : Optional.empty();
    }

    private boolean testExecutor(FixedSessionExecutor executor) {
        return false;
    }
}
