package dev.comfast.cf.common.events;
import dev.comfast.cf.common.errors.CfFrameworkError;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EventsManager<ContextObject> {
    private final Map<String, EventListener<ContextObject>> listeners = new HashMap<>();

    public void addListener(String listenerName, EventListener<ContextObject> listener) {
        if(listenerName.isEmpty()) throw new CfFrameworkError("Listener name should be not empty.");
        if(listener == null) throw new CfFrameworkError("Listener should not be null.");

        listeners.put(listenerName, listener);
    }

    public void removeListener(String name) {
        listeners.remove(name);
    }

    public void action(ContextObject ctx, String name, Runnable actionFunc) {
        action(new BeforeEvent<>(ctx, name), actionFunc);
    }

    public void action(BeforeEvent<ContextObject> event, Runnable actionFunc) {
        action(event, () -> {actionFunc.run(); return "done";});
    }

    public <T> T action(BeforeEvent<ContextObject> event, Supplier<T> actionFunc) {
        AfterEvent<ContextObject> afterEvent = null;
        for(var l : listeners.values()) l.before(event);
        try {
            var result = actionFunc.get();
            afterEvent = event.passed(result);
            return result;
        } catch(Throwable e) {
            afterEvent = event.failed(e);
            throw e;
        } finally {
            for(var l : listeners.values()) l.after(afterEvent);
        }
    }
}
