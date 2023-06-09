package dev.comfast.cf.common.errors;
import static java.lang.String.format;

/**
 * Indicates that something went wrong in the framework. Generally should not happen in production.
 */
public class CfFrameworkError extends RuntimeException {
    public CfFrameworkError(String message, Object... args) {
        super(format(message, args));
    }

    public CfFrameworkError(String message, Throwable cause, Object... args) {
        super(format(message, args), cause);
    }
}
