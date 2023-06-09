package dev.comfast.cf.common.events;
import dev.comfast.util.time.StopwatchTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Getter
@RequiredArgsConstructor
public class AfterEvent<EventContext> {
    public final EventContext context;
    public final String actionName;
    public final Object[] actionParams;
    public final StopwatchTime time;
    public final EventStatus status;
    public final Object result;
    public final Throwable error;

    public Duration getDuration() {
        return time.getDuration();
    }

    @Override public String toString() {
        return String.format("%s(%s)%s %s%s",
            actionName,
            Stream.of(actionParams).map(Object::toString).collect(joining(", ")),
            result != null ? "=>" + result : "",
            status.toString(), time != null ? " (" + time + ")" : ""
        );
    }
}
