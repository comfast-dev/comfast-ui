package dev.comfast.cf.common.conditions;
import dev.comfast.cf.CfFoundLocator;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.errors.ConditionFailed;
import dev.comfast.cf.common.errors.ElementFindFail;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Function;

/**
 * Represents a condition that can be checked against a CfLocator.
 */
@Builder(toBuilder = true)
public class ConditionImpl implements Condition {
    @Getter final String name;
    @Getter private final boolean passIfNotFound;
    private final boolean negated;
    private final Function<CfFoundLocator, Boolean> testFunc;

    /**
     * Executes test against given locator.
     * Doesn't throw any exception
     */
    public boolean test(CfLocator el) {
        try {
            return testFunc.apply(el.find());
        } catch(Exception e) {
            return false;
        }
    }

    public void expect(CfLocator locator) {
        try {
            CfFoundLocator foundEl = locator.find();
            boolean result = negated ^ testFunc.apply(foundEl);
            if(!result) throw new ConditionFailed(
                "Condition: \"%s\" failed for element: %s", name, foundEl);
        } catch(ElementFindFail e) {
            if(negated ^ passIfNotFound) return;
            throw new ConditionFailed(
                "Condition: \"%s\" failed. Element not found:%n%s", e, name, e.getPointer());
        } catch(ConditionFailed e) {
            throw e;
        } catch(Exception e) {
            throw new ConditionFailed(
                "Condition: \"%s\" failed with exception. %nElement: %s", e, name);
        }
    }

    public ConditionImpl negate(String newName) {
        return toBuilder().name(newName).negated(!negated).build();
    }

    @Override public String toString() {
        return name;
    }
}
