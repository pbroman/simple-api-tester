package dev.pbroman.simple.api.tester.util;

import dev.pbroman.simple.api.tester.records.Condition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.pbroman.simple.api.tester.util.ConditionResolver.BLANK;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.CONTAINS;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.EMPTY;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.ENDS_WITH;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.EQUALS;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.EQUALS_IGNORE_CASE;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.EQUAL_TO;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.GREATER_THAN;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.GREATER_THAN_OR_EQUAL;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.FALSE;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.TRUE;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.LESS_THAN;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.LESS_THAN_OR_EQUAL;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.MATCHES;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.NULL;
import static dev.pbroman.simple.api.tester.util.ConditionResolver.STARTS_WITH;
import static org.junit.jupiter.api.Assertions.*;

class ConditionResolverTest {

    @Test
    void happyStringPaths() {
        assertTrue(ConditionResolver.resolve(new Condition(NULL, null, null)));
        assertTrue(ConditionResolver.resolve(new Condition("isnull", null, null)));
        assertTrue(ConditionResolver.resolve(new Condition("notNull", "a", null)));
        assertTrue(ConditionResolver.resolve(new Condition("notisNull", "", null)));
        assertTrue(ConditionResolver.resolve(new Condition(EMPTY, "", null)));
        assertTrue(ConditionResolver.resolve(new Condition("isEmpty", "", null)));
        assertTrue(ConditionResolver.resolve(new Condition("notEmpty", "a", null)));
        assertTrue(ConditionResolver.resolve(new Condition("!empty", "a", null)));
        assertTrue(ConditionResolver.resolve(new Condition("!isEmpty", "a", null)));
        assertTrue(ConditionResolver.resolve(new Condition(EQUALS, "foo", "foo")));
        assertTrue(ConditionResolver.resolve(new Condition("notEquals", "foo", "bar")));
        assertTrue(ConditionResolver.resolve(new Condition("!equals", "foo", "bar")));
        assertTrue(ConditionResolver.resolve(new Condition(STARTS_WITH, "foo", "fo")));
        assertTrue(ConditionResolver.resolve(new Condition("!startsWith", "foo", "a")));
        assertTrue(ConditionResolver.resolve(new Condition(ENDS_WITH, "foo", "oo")));
        assertTrue(ConditionResolver.resolve(new Condition(MATCHES, "foo", "f.*")));
        assertTrue(ConditionResolver.resolve(new Condition("notMatches", "foo", "b.*")));
        assertTrue(ConditionResolver.resolve(new Condition("!matches", "foo", "b.*")));
        assertTrue(ConditionResolver.resolve(new Condition(CONTAINS, "foo", "o")));
        assertTrue(ConditionResolver.resolve(new Condition("notContains", "foo", "a")));
        assertTrue(ConditionResolver.resolve(new Condition("!contains", "foo", "a")));
        assertTrue(ConditionResolver.resolve(new Condition(EQUALS_IGNORE_CASE, "foo", "fOO")));
        assertTrue(ConditionResolver.resolve(new Condition(BLANK, "   ", null)));
        assertTrue(ConditionResolver.resolve(new Condition("ISBLANK", "   ", null)));
        assertTrue(ConditionResolver.resolve(new Condition("!blank", "foo", null)));
        assertTrue(ConditionResolver.resolve(new Condition("notblank", "foo", null)));
        assertTrue(ConditionResolver.resolve(new Condition("notisblank", "foo", null)));
    }

    @Test
    void happyIntegerPaths() {
        assertTrue(ConditionResolver.resolve(new Condition("!null", 1, null)));
        assertTrue(ConditionResolver.resolve(new Condition(EQUAL_TO, 1, 1)));
        assertTrue(ConditionResolver.resolve(new Condition("!=", 1, 2)));
        assertTrue(ConditionResolver.resolve(new Condition(GREATER_THAN_OR_EQUAL, 1, 1)));
        assertTrue(ConditionResolver.resolve(new Condition(GREATER_THAN_OR_EQUAL, 2, 1)));
        assertTrue(ConditionResolver.resolve(new Condition(LESS_THAN_OR_EQUAL, 1, 1)));
        assertTrue(ConditionResolver.resolve(new Condition(LESS_THAN_OR_EQUAL, 1, 2)));
        assertTrue(ConditionResolver.resolve(new Condition(GREATER_THAN, 2, 1)));
        assertTrue(ConditionResolver.resolve(new Condition(LESS_THAN, 1, 2)));
    }

    @Test
    void happyDoublePaths() {
        assertTrue(ConditionResolver.resolve(new Condition("notnull", 1.0, null)));
        assertTrue(ConditionResolver.resolve(new Condition(EQUAL_TO, 1.0, 1.0)));
        assertTrue(ConditionResolver.resolve(new Condition("!=", 1.0, 1.1)));
        assertTrue(ConditionResolver.resolve(new Condition(GREATER_THAN_OR_EQUAL, 1.0, 1.0)));
        assertTrue(ConditionResolver.resolve(new Condition(GREATER_THAN_OR_EQUAL, 1.1, 1.0)));
        assertTrue(ConditionResolver.resolve(new Condition(LESS_THAN_OR_EQUAL, 1.0, 1.0)));
        assertTrue(ConditionResolver.resolve(new Condition(LESS_THAN_OR_EQUAL, 1.0, 1.1)));
        assertTrue(ConditionResolver.resolve(new Condition(GREATER_THAN, 1.1, 1.0)));
        assertTrue(ConditionResolver.resolve(new Condition(LESS_THAN, 1.0, 1.1)));
    }

    @Test
    void happyBooleanPaths() {
        assertTrue(ConditionResolver.resolve(new Condition("notNull", Boolean.TRUE, null)));
        assertTrue(ConditionResolver.resolve(new Condition(TRUE, Boolean.TRUE, null)));
        assertTrue(ConditionResolver.resolve(new Condition(FALSE, Boolean.FALSE, null)));
    }

    @Test
    void happyMixedPaths() {
        assertTrue(ConditionResolver.resolve(new Condition(EQUALS,"1", 1)));
        assertTrue(ConditionResolver.resolve(new Condition(EQUALS, "true", Boolean.TRUE)));
        assertTrue(ConditionResolver.resolve(new Condition(EQUALS, "1.0", 1.0)));
        assertTrue(ConditionResolver.resolve(new Condition(EQUALS,1, "1")));
        assertTrue(ConditionResolver.resolve(new Condition(EQUALS, Boolean.TRUE, "true")));
        assertTrue(ConditionResolver.resolve(new Condition(EQUALS, 1.0, "1.0")));
        assertTrue(ConditionResolver.resolve(new Condition(EQUAL_TO, 1.0, "1.0")));
        assertTrue(ConditionResolver.resolve(new Condition(EQUAL_TO, 1.0, 1)));
        assertTrue(ConditionResolver.resolve(new Condition(TRUE, "true", null)));
    }

    @Test
    void unhappyPaths() {
        assertThrows(IllegalArgumentException.class,
                () -> ConditionResolver.resolve(new Condition(EQUALS, 1, null)),
                "The other may only be null for operations 'null', 'notNull', 'empty', 'notEmpty', 'isTrue', or 'isFalse'"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ConditionResolver.resolve(new Condition(EQUALS, List.of(), null)),
                "The value class must be String, Integer, Double, or Boolean"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ConditionResolver.resolve(new Condition("foo", "", "")),
                "Unsupported string operation should throw exception"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ConditionResolver.resolve(new Condition("foo", Boolean.FALSE, null)),
                "Unsupported boolean operation should throw exception"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ConditionResolver.resolve(new Condition("?", 1, 1)),
                "Unsupported integer operation should throw exception"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ConditionResolver.resolve(new Condition("?", 1.0, 1.0)),
                "Unsupported double operation should throw exception"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ConditionResolver.resolve(new Condition(TRUE, 1, null)),
                "Unsupported double operation should throw exception"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ConditionResolver.resolve(new Condition(EQUAL_TO, true, "true")),
                "Unsupported double operation should throw exception"
        );

    }
}