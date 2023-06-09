package dev.comfast.cf.common.events;
import dev.comfast.util.time.Stopwatch;
import lombok.Getter;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Getter
public class BeforeEvent<EventContext> {
    public final EventContext context;
    public final String actionName;
    public final Object[] actionParams;
    public final EventStatus status = EventStatus.IN_PROGRESS;
    private final Stopwatch stopwatch = new Stopwatch();

    public BeforeEvent(EventContext context, String actionName, Object... actionParams) {
        this.context = context;
        this.actionName = actionName;
        this.actionParams = actionParams;
    }

    /**
     * Creates PASSED AfterEvent with given result.
     */
    public AfterEvent<EventContext> passed(Object result) {
        return new AfterEvent<>(context, actionName, actionParams,
            stopwatch.time(), EventStatus.PASSED, result, null);
    }

    /**
     * Creates failed AfterEvent with given Throwable.
     */
    public AfterEvent<EventContext> failed(Throwable error) {
        return new AfterEvent<>(context, actionName, actionParams,
            stopwatch.time(), EventStatus.FAILED, null, error);
    }

    @Override public String toString() {
        return String.format("%s(%s) IN PROGRESS... %s",
            actionName,
            Stream.of(actionParams).map(Object::toString).collect(joining(", ")),
            stopwatch.time()
        );
    }
}
