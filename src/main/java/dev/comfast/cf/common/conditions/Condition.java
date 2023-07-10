package dev.comfast.cf.common.conditions;
import dev.comfast.cf.CfLocator;

/**
 * Represents a condition that can be checked on an element.
 * Conditions are used in {@link CfLocator#should(Condition)} method.
 *
 * @see dev.comfast.cf.common.conditions.Conditions predefined conditions
 * @see dev.comfast.cf.CfLocator#should(Condition)
 */
public interface Condition {
    /**
     * @return name of condition
     */
    String getName();
    /**
     * throw if condition not pass
     */
    void expect(CfLocator locator);
    /**
     * @return negated condition with new name
     */
    Condition negate(String newName);
    /**
     * @return should pass if given element not exists
     */
    default boolean isPassIfNotFound() {
        return false;
    }
    /**
     * @return negated condition
     */
    default Condition negate() {
        return negate(getName().startsWith("not ")
                      ? getName().substring(4)
                      : "not " + getName());
    }
}
