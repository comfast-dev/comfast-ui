package dev.comfast.cf.common.conditions;
import dev.comfast.cf.CfFoundLocator;
import dev.comfast.cf.CfLocator;
import lombok.experimental.FieldDefaults;

import java.util.function.Function;

import static dev.comfast.cf.common.utils.TextUtils.flatText;
import static java.lang.String.format;

/**
 * Predefined conditions and factory methods.
 */
@FieldDefaults(makeFinal = true)
public class Conditions {
    /**
     * Pass if element exist in DOM. Doesn't check visibility.
     */
    public static Condition exists = condition("exists", el -> true);
    /**
     * Pass if element is visible.
     */
    public static Condition appear = condition("appear", CfLocator::isDisplayed);
    /**
     * Pass if element is not visible.
     */
    public static Condition disappear = not(appear, "disappear");
    /**
     * Pass if element is disabled
     */
    public static Condition beDisabled = hasAttribute("disabled", "true");
    /**
     * Pass if element is enabled
     */
    public static Condition beEnabled = not(beDisabled);
    /**
     * Pass  if element can be clicked. i.e. it exists, visible and enabled
     */
    public static Condition beClickable = allMatch(
        exists, appear, beEnabled
    );

    /**
     * Condition that pass if given testFunction pass.
     *
     * @param name of condition
     * @param testFunc function that will be executed against element
     * @return Condition implementation
     */
    public static Condition condition(String name, Function<CfFoundLocator, Boolean> testFunc) {
        return ConditionImpl.builder()
            .name(name).testFunc(testFunc).build();
    }

    /**
     * @return new Condition that negates given one. New name will have prefix: 'not '
     * @see Condition#negate()
     */
    public static Condition not(Condition condition) {
        return condition.negate();
    }

    /**
     * @return new Condition that negates given one.
     * @see Condition#negate(String)
     */
    public static Condition not(Condition condition, String newName) {
        return condition.negate(newName);
    }

    /**
     * @return new Condition that pass if all given conditions pass
     */
    public static Condition allMatch(Condition... conditions) {
        return new AllMatch(conditions);
    }

    /**
     * @return Condition that will pass if element have given attribute not null
     * @see CfLocator#getAttribute(String)
     */
    public static Condition hasAttribute(String name) {
        return condition(format("has attribute: '%s'", name),
            el -> el.getAttribute(name) != null);
    }

    /**
     * @return Condition that will pass if element have given attribute equals expectedValue
     * @see CfLocator#getAttribute(String)
     */
    public static Condition hasAttribute(String name, String expectedValue) {
        return condition(format("has attribute %s='%s'", name, expectedValue),
            el -> el.getAttribute(name).equals(expectedValue));
    }

    /**
     * @return Condition that will pass if element have exact given text
     * @see CfLocator#getText()
     */
    public static Condition hasText(String expectedText) {
        return condition(format("has text: '%s'", expectedText),
            el -> flatText(el.getText()).equals(flatText(expectedText)));
    }

    /**
     * @return Condition that will pass if element have exact given text
     * @see CfLocator#getText()
     */
    public static Condition containText(String expectedText) {
        return condition(format("contain text: '%s'", expectedText),
            el -> flatText(el.getAttribute("textContent")).contains(flatText(expectedText)));
    }

    /**
     * @return Condition that will pass if element have given css class
     * @see CfLocator#hasClass(String)
     */
    public static Condition hasCssClass(String expectedClass) {
        return condition(format("has class: '%s'", expectedClass),
            el -> el.hasClass(expectedClass));
    }

    /**
     * @return Condition that will pass if matched element have given count.
     * @see CfLocator#count()
     */
    public static Condition haveCount(int expectedCount) {
        return condition(format("have %d elements", expectedCount),
            el -> el.count() == expectedCount);
    }
}
