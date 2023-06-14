package dev.comfast.cf.common.conditions;
import dev.comfast.cf.CfFoundLocator;
import dev.comfast.cf.CfLocator;
import lombok.experimental.FieldDefaults;

import java.util.function.Function;

import static dev.comfast.cf.common.utils.TextUtils.flatText;
import static java.lang.String.format;

@FieldDefaults(makeFinal = true)
public class Conditions {
    public static Condition condition(String name, Function<CfFoundLocator, Boolean> testFunc) {
        return ConditionImpl.builder()
            .name(name)
            .testFunc(testFunc)
            .build();
    }

    public static Condition not(Condition condition) {
        return condition.negate();
    }

    public static Condition not(Condition condition, String newName) {
        return condition.negate(newName);
    }

    public static Condition allMatch(Condition... conditions) {
        return new AllMatch(conditions);
    }

    public static Condition exists = condition("exists", el -> true);
    public static Condition appear = condition("appear", CfLocator::isDisplayed);
    public static Condition disappear = not(appear, "disappear");
    public static Condition beDisabled = hasAttribute("disabled", "true");
    public static Condition beEnabled = not(beDisabled);
    public static Condition beClickable = allMatch(
        exists, appear, beEnabled
    );

    public static Condition hasText(String expectedText) {
        return condition(format("has text: '%s'", expectedText),
            el -> flatText(el.getText()).equals(flatText(expectedText)));
    }

    public static Condition hasAttribute(String name) {
        return condition(format("has attribute %s", name),
            el -> el.getAttribute(name) != null);
    }

    public static Condition hasAttribute(String name, String expectedValue) {
        return condition(format("has attribute %s=%s", name, expectedValue),
            el -> el.getAttribute(name).equals(expectedValue));
    }

    public static Condition hasCssClass(String expectedClass) {
        return condition(format("has class: %s", expectedClass),
            el -> el.hasClass(expectedClass));
    }

    public static Condition haveCount(int expectedCount) {
        return condition(format("have %d elements", expectedCount),
            el -> el.count() == expectedCount);
    }
}
