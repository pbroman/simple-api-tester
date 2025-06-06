package dev.pbroman.brat.core.records.result;

import dev.pbroman.brat.core.records.Condition;

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
