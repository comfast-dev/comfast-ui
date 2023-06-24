package dev.comfast.cf.common.utils;
import dev.comfast.experimental.events.EventListener;
import dev.comfast.experimental.events.model.AfterEvent;
import org.openqa.selenium.remote.Command;

/**
 * Print out all internal WebDriver events with its times.
 * How to use: {@code CfApi.driverEvents.addListener("tracer", new Tracer())}
 * <p>Example console output:</p>
 * <pre>
 * AfterEvent findElement([css selector, #clicker span]) (11.1ms)
 * AfterEvent getElementAttribute([ABE324DFBAAD5DA9864B1CF4F74ACCB2_element_132, innerText]) (23.1ms)
 * AfterEvent findElement([css selector, #clicker button]) (9.63ms)
 * AfterEvent clickElement([ABE324DFBAAD5DA9864B1CF4F74ACCB2_element_133]) (28.5ms)
 * AfterEvent findElement([css selector, #clicker button]) (8.36ms)
 * AfterEvent clickElement([ABE324DFBAAD5DA9864B1CF4F74ACCB2_element_133]) (24.7ms)
 * AfterEvent findElement([css selector, #clicker button]) (8.75ms)
 * AfterEvent clickElement([ABE324DFBAAD5DA9864B1CF4F74ACCB2_element_133]) (22.8ms)
 * </pre>
 */
public class Tracer implements EventListener<Command> {

    @Override public void after(AfterEvent<Command> event) {
        String str = event.toString().replaceAll("\\s+", " ");
        if(str.length() > 150) {
            str = str.substring(0, 140) + event.time;
        }
        System.out.println(str);
    }
}
