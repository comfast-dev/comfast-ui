package dev.comfast.cf.common.errors;
import static java.lang.String.format;

public class ConditionFailed extends RuntimeException {
    public ConditionFailed(String message, Object... args) {
        super(format(message, args));
    }

    public ConditionFailed(String message, Throwable cause, Object... args) {
        super(format(message, args), cause);
    }
}
