package dev.comfast.cf.common.conditions;
import org.junit.jupiter.api.Test;

class CfAnyConditionTest {
    /**
     * | false | false | null  | continue
     * | false | false | null  | continue
     * | false | true  | null  | return true
     * | false | true  | null  | return true
     * | true  | false | false | continue
     * | true  | false | true  | return true
     * | true  | true  | false | continue
     * | true  | true  | true  | return true
     */
    @Test void tableOfTruth() {

    }
}