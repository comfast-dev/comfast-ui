package dev.comfast.integration;
import dev.comfast.cf.CfApi;
import dev.comfast.cf.CfLocator;
import dev.comfast.experimental.events.EventListener;
import dev.comfast.experimental.events.model.AfterEvent;
import dev.comfast.experimental.events.model.BeforeEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static dev.comfast.cf.CfApi.$;
import static org.assertj.core.api.Assertions.assertThat;

class CaptureEventsTest {
    static BeforeEvent<CfLocator> beforeEvent;
    static AfterEvent<CfLocator> afterEvent;

    @BeforeAll
    static void init() {
        CfApi.locatorEvents.addListener("myListener", new EventListener<>() {
            @Override public void before(BeforeEvent<CfLocator> event) {
                beforeEvent = event;
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
        assertThat(afterEvent).isNotNull();
        assertThat(afterEvent.actionName).isEqualTo("count");
        assertThat(afterEvent.result).isEqualTo(1);
        assertThat(beforeEvent).isNotNull();
    }
}
