package dev.comfast.cf.se.infra;
import lombok.SneakyThrows;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.codec.w3c.W3CHttpCommandCodec;
import org.openqa.selenium.remote.codec.w3c.W3CHttpResponseCodec;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;

/**
 * Used to create custom RemoteWebDriver instance
 */
public class FixedSessionExecutor extends HttpCommandExecutor {
    private final SessionId sessionId;

    public FixedSessionExecutor(URL browserAddress, SessionId sessionId) {
        super(browserAddress);
        this.sessionId = sessionId;
    }

    @Override
    public Response execute(Command command) throws IOException {
        return command.getName().equals("newSession")
               ? mockNewSession()
               : super.execute(command);
    }

    private Response mockNewSession() {
        Response response;
        response = new Response(sessionId);
        response.setStatus(0);
        response.setValue(Map.of());

        setPrivateParentClassField("commandCodec", new W3CHttpCommandCodec());
        setPrivateParentClassField("responseCodec", new W3CHttpResponseCodec());
        return response;
    }

    @SneakyThrows
    private void setPrivateParentClassField(String fieldName, Object fieldValue) {
        Field field = this.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(this, fieldValue);
    }
}
