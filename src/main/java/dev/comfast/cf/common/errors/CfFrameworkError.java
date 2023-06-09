package dev.comfast.cf.common.errors;
import static java.lang.String.format;

public class CfFrameworkError extends RuntimeException {
    public CfFrameworkError(String message, Object... args) {
        super(format(message, args));
    }

    public CfFrameworkError(String message, Throwable cause, Object... args) {
        super(format(message, args), cause);
    }
}
