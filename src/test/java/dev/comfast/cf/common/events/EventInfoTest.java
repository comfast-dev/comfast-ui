package dev.comfast.cf.common.events;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventInfoTest {
    final String MY_CONTEXT = "HELLO";
    final String MY_ACTION = "click";
    final String MY_RESULT = "WORLD";

    @Test void createEvent() {
        var event = new BeforeEvent<>(MY_CONTEXT, MY_ACTION, "lol");
        assertAll(
            () -> assertEquals(MY_CONTEXT, event.getContext()),
            () -> assertEquals(MY_ACTION, event.getActionName()),
            () -> assertEquals("lol", event.getActionParams()[0]),

            () -> assertEquals(EventStatus.IN_PROGRESS, event.getStatus())
        );
    }

    @Test void passEvent() {
        var beforeEvent = new BeforeEvent<>(MY_CONTEXT, MY_ACTION);
        var event = beforeEvent.passed(MY_RESULT);

        assertAll(
            () -> assertEquals(EventStatus.PASSED, event.getStatus()),
            () -> assertEquals(MY_RESULT, event.getResult()),
            () -> assertTrue(event.getDuration().toNanos() > 0, "duration > 0"),
            () -> assertEquals(event.getDuration().toNanos(), event.getDuration().toNanos()),
            () -> assertNull(event.getError())
        );
    }

    @Test void failEvent() {
        var beforeEvent = new BeforeEvent<>(MY_CONTEXT, MY_ACTION);
        var event = beforeEvent.failed(new RuntimeException("oh no"));
        assertAll(
            () -> assertEquals(EventStatus.FAILED, event.getStatus()),
            () -> assertNull(event.getResult()),
            () -> assertTrue(event.getDuration().toNanos() > 0, "duration > 0"),
            () -> assertEquals("oh no", event.getError().getMessage())
        );
    }
}