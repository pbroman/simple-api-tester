package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

import java.util.Objects;

import static dev.pbroman.simple.api.tester.util.Constants.DEFAULT_MAX_ATTEMPTS;

public record RepeatUntil(Condition condition, Integer maxAttempts, Integer waitBetweenAttempts, String messageOnFail) {

    public RepeatUntil {
        Objects.requireNonNull(condition, "Condition cannot be null");
        if (maxAttempts == null) {
            maxAttempts = DEFAULT_MAX_ATTEMPTS;
        }
    }

    public RepeatUntil interpolated(RuntimeData runtimeData) {
        var interpolatedCondition = condition.interpolated(runtimeData);
        var interpolatedMaxAttempts = (Integer) Interpolation.interpolate(maxAttempts, runtimeData);
        var interpolatedWaitBetweenAttempts = (Integer) Interpolation.interpolate(waitBetweenAttempts, runtimeData);
        return new RepeatUntil(interpolatedCondition, interpolatedMaxAttempts, interpolatedWaitBetweenAttempts, messageOnFail);
    }
}
