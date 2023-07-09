package dev.comfast.cf.common.errors;
import static java.lang.String.format;

/**
 * Indicates that condition was not met.
 */
public class ConditionFailed extends RuntimeException {
    public ConditionFailed(String message, Object... args) {
        super(format(message, args));
    }

    public ConditionFailed(String message, Throwable cause, Object... args) {
        super(format(message, args), cause);
    }

    @Override public String toString() {
        return getMessage();
    }
}
