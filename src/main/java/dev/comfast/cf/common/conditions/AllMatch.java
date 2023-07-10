package dev.comfast.cf.common.conditions;
import dev.comfast.cf.CfFoundLocator;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.errors.ConditionFailed;
import dev.comfast.cf.common.errors.ElementFindFail;
import lombok.Getter;

import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 * Condition that pass if all sub-conditions are met.
 */
public class AllMatch implements Condition {
    @Getter private final String name;
    private final Condition[] subConditions;
    /**
     * true if all conditions allow pass if not found
     */
    @Getter private final boolean passIfNotFound;

    public AllMatch(Condition... conditions) {
        this.name = format("match all [%s]",
            stream(conditions).map(Condition::getName).collect(joining(", ")));
        this.subConditions = conditions;
        passIfNotFound = Stream.of(conditions)
            .allMatch(Condition::isPassIfNotFound);
    }

    @Override public void expect(CfLocator locator) {
        CfFoundLocator foundElement;
        try {
            foundElement = locator.find();
        } catch(ElementFindFail e) {
            if(passIfNotFound) return;
            throw new ConditionFailed("Condition '%s' failed. Element not found: %s", name, e.getPointer());
        }

        StringBuilder resultMessage = new StringBuilder("Should match all: ");
        for(var condition : subConditions) {
            try {
                condition.expect(foundElement);
                resultMessage.append("\nPASSED: ").append(condition);
            } catch(Exception e) {
                resultMessage.append("\nFAILED: ").append(condition);
                throw new ConditionFailed(resultMessage.toString());
            }
        }
    }

    @Override public Condition negate(String newName) {
        throw new UnsupportedOperationException();
    }
}
