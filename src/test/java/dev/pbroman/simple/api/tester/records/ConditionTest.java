package dev.pbroman.simple.api.tester.records;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConditionTest {

    @Test
    void happyPaths() {
        assertDoesNotThrow( () -> new Condition("null", 1, null) );
        assertDoesNotThrow( () -> new Condition("null", "", null) );
        assertDoesNotThrow( () -> new Condition("equals", 1, 2) );
        assertDoesNotThrow( () -> new Condition("equals", "foo", "bar") );
    }

    @Test
    void unhappyPaths() {
        assertThrows(NullPointerException.class,
                () -> new Condition(null, null, null)
        );
        assertThrows(NullPointerException.class,
                () -> new Condition(null, "", null)
        );
    }

}