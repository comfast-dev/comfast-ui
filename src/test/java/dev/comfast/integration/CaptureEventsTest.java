package dev.comfast.integration;
import dev.comfast.cf.CfApi;
import dev.comfast.cf.CfLocator;
import dev.comfast.experimental.events.EventListener;
import dev.comfast.experimental.events.model.AfterEvent;
import dev.comfast.experimental.events.model.Event;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;

public class CaptureEventsTest {
    static Event<CfLocator> beforeEvent;
    static AfterEvent<CfLocator> afterEvent;

    @BeforeAll
    static void init() {
        CfApi.locatorEvents.addListener("myListener", new EventListener<>() {
            @Override public void before(Event<CfLocator> event) {
                System.out.println(event);
            }

            @Override public void after(AfterEvent<CfLocator> event) {
                afterEvent = event;
            }
        });
    }

    @AfterAll
    static void clean() {
        CfApi.locatorEvents.removeListener("myListener");
    }

    @Test void captureEvents() {

        $("body").count();
        System.out.println(beforeEvent);
        System.out.println(afterEvent);
    }
}
