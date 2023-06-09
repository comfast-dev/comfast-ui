package dev.comfast.cf.common.events;
public interface EventListener<LogContext> {
    default void before(BeforeEvent<LogContext> event) {}
    default void after(AfterEvent<LogContext> event) {}
}
