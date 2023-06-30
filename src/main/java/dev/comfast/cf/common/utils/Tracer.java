package dev.comfast.cf.common.utils;
import dev.comfast.experimental.events.EventListener;
import dev.comfast.experimental.events.model.AfterEvent;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Response;

import java.util.Map;

import static dev.comfast.rgx.RgxApi.rgx;
import static dev.comfast.util.Utils.trimString;
import static java.lang.String.format;

/**
 * Print out all internal WebDriver events with its times.
 * How to use: {@code CfApi.driverEvents.addListener("tracer", new Tracer())}
 * <p>Example console output:</p>
 * <pre>
 * findElement               | my-div                                   | success              | 10.8ms
 * findChildElement          | _element_67 >> h3                        | no such element      | 13.7ms
 * executeScript             | return arguments[0].shadowRoot           | success              | 10.7ms
 * findElementFromShadowRoot | _element_68 >> h3                        | success              | 9.96ms
 * </pre>
 */
public class Tracer implements EventListener<Command> {
    @Override public void after(AfterEvent<Command> event) {
        System.out.println(formatLogMessage(event));
    }

    protected String formatLogMessage(AfterEvent<Command> event) {
        return format("%-25s | %-40s | %-20s | %s%n",
            event.actionName,
            trimString(formatPayload(event.context), 40),
            ((Response) event.result).getState(),
            event.time);
    }

    /**
     * @param context Selenium command
     * @return Command payload String
     */
    private Object formatPayload(Command context) {
        //noinspection unchecked
        Map<String, Object> params = (Map<String, Object>) context.getParameters();

        if(params.containsKey("value")) {
            if(params.containsKey("id")) return trimId(params.get("id")) + params.get("value");
            if(params.containsKey("shadowId")) return trimId(params.get("shadowId")) + params.get("value");
            return params.get("value");
        }
        if(params.containsKey("name")) return trimId(params.get("id")) + params.get("name");
        if(params.containsKey("script")) return params.get("script");

        return "";
    }

    /**
     * @return fragment of Selenium ID like: '_element_22'
     */
    private String trimId(Object seleniumId) {
        String match = rgx("(_element_\\d+)$").match(seleniumId.toString()).getOrElse("");

        return match.isEmpty() ? "" : match + " >> ";
    }
}