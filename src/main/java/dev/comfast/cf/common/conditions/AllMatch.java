package dev.comfast.cf.common.conditions;
import dev.comfast.cf.CfLocator;
import dev.comfast.cf.common.errors.ConditionFailed;
import lombok.Getter;

import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class AllMatch implements Condition {
    @Getter private final String name;
    private final Condition[] conditions;

    public AllMatch(Condition... conditions) {
        this.name = format("match all [%s]",
            stream(conditions).map(Condition::getName).collect(joining(", ")));
        this.conditions = conditions;
    }

    @Override public void expect(CfLocator locator) {
        var found = locator.tryFind();
        if(found.isEmpty()) {
            boolean passIfNotFound = Stream.of(conditions)
                .allMatch(c -> c instanceof ConditionImpl && ((ConditionImpl) c).isPassIfNotFound());

            if(passIfNotFound) return;
            throw new ConditionFailed("Condition '%s' failed. Element not found", name);
        }

        StringBuilder result = new StringBuilder("Should match all: ");
        for(var c : conditions) {
            try {
                c.expect(found.get());
                result.append("\nPASSED: ").append(c);
            } catch(Exception e) {
                result.append("\nFAILED: ").append(c);
                throw new ConditionFailed(result.toString());
            }
        }
    }

    @Override public Condition negate() {
        throw new UnsupportedOperationException();
    }

    @Override public Condition negate(String newName) {
        throw new UnsupportedOperationException();
    }
}
