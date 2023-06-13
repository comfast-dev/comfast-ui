package dev.comfast.integration;
import dev.comfast.cf.CfApi;
import dev.comfast.cf.CfLocator;
import dev.comfast.experimental.events.EventListener;
import dev.comfast.experimental.events.model.AfterEvent;
import dev.comfast.experimental.events.model.BeforeEvent;
import dev.comfast.experimental.events.model.FailedEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.Command;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.CfApi.open;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Examples depend on external site, not need to run it before every release")
class ExamplesAdvancedTest {
    @Test void example7_highLevelEvents() {
        // It is possible to connect through events to any action performed by the library
        // for example, to log all performed actions, or react to errors

        // 1. High level events (locatorEvents)
        @SuppressWarnings("unused") class MyLogListener implements EventListener<CfLocator> {
            @Override public void before(BeforeEvent<CfLocator> event) {
                System.out.println("Attempt to run: " + event.actionName);
            }

            @Override public void after(AfterEvent<CfLocator> event) {
                System.out.println(
                    "Action " + event.actionName + " finished in time " + event.time);
            }

            @Override public void failed(FailedEvent<CfLocator> event) {
                // You can use locator to examine what's happened
                CfLocator relatedLocator = event.context;

                // or read the exception
                Throwable err = event.error;

                System.out.println("Locator: " + event.context + "failed with error: " + event.error);
            }
        }

        //add listener
        CfApi.locatorEvents.addListener("myListener", new MyLogListener());
        doSomeActions();
        assertNotNull(CfApi.locatorEvents);

        //this code will print:
        //   Attempt to run: getAttribute
        //   Action getAttribute finished in time 61.4ms
        //   Attempt to run: click
        //   Action click finished in time 344ms
    }

    @Test void example8_lowLevelEvents() {
        // Low level events from WebDriver also can be caught (driverEvents)
        class FindElementCounter implements EventListener<Command> {

            long summaryFindTimeMs = 0;
            long summaryFindCounts = 0;
            @Override public void after(AfterEvent<Command> event) {
                if(event.actionName.equals("findElement") || event.actionName.equals("findChildElement")) {
                    summaryFindTimeMs += event.time.getMillis();
                    summaryFindCounts++;
                }
            }
        }

        //add listener
        var simpleCounter = new FindElementCounter();
        CfApi.driverEvents.addListener("myFindElementCounter", simpleCounter);

        doSomeActions();

        // use the listener
        System.out.printf("Total findElement calls: %d taken %dms%n", simpleCounter.summaryFindCounts, simpleCounter.summaryFindTimeMs);
        assertTrue(simpleCounter.summaryFindTimeMs > 0);

        //this code will print:
        // Total findElement calls: 4 taken 51ms
    }

    private static void doSomeActions() {
        open("https://www.wikipedia.org");
        $(".central-featured >> .//a[.//strong[text()='English']]").getText();
        $(".central-featured >> .//a[.//strong[text()='English']]").click();
    }
}
