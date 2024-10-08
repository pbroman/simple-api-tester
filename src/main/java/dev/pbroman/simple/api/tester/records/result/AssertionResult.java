package dev.pbroman.simple.api.tester.records.result;

import dev.pbroman.simple.api.tester.records.Condition;

public record AssertionResult(Condition condition, Condition interpolatedCondition, boolean passed) {

    public AssertionResult {
        if (condition == null) {
            throw new IllegalArgumentException("Condition cannot be null");
        }
        if (interpolatedCondition == null) {
            throw new IllegalArgumentException("Interpolated condition cannot be null");
        }
    }

}
