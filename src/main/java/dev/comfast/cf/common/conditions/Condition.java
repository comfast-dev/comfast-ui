package dev.comfast.cf.common.conditions;
import dev.comfast.cf.CfLocator;

public interface Condition {
    String getName();
    void expect(CfLocator locator);
    Condition negate();
    Condition negate(String newName);
}
