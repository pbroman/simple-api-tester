package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.api.ConfigRecord;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.result.ValidationType;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

import java.util.ArrayList;
import java.util.List;

import static dev.pbroman.simple.api.tester.util.Constants.DEFAULT_MAX_ATTEMPTS;

public record RepeatUntil(Condition condition, Integer maxAttempts, Integer waitBetweenAttempts, String messageOnFail) implements ConfigRecord {

    public RepeatUntil {
        if (maxAttempts == null) {
            maxAttempts = DEFAULT_MAX_ATTEMPTS;
        }
    }

    @Override
    public List<Validation> validate() {
        var validations = new ArrayList<Validation>();
        if (condition == null) {
            validations.add(new Validation(this.getClass().getSimpleName(), null, "Condition for repeatUntil cannot be null", ValidationType.FAIL));
        }
        return validations;
    }

    @Override
    public RepeatUntil interpolated(RuntimeData runtimeData) {
        var interpolatedCondition = condition.interpolated(runtimeData);
        var interpolatedMaxAttempts = (Integer) Interpolation.interpolate(maxAttempts, runtimeData);
        var interpolatedWaitBetweenAttempts = (Integer) Interpolation.interpolate(waitBetweenAttempts, runtimeData);
        return new RepeatUntil(interpolatedCondition, interpolatedMaxAttempts, interpolatedWaitBetweenAttempts, messageOnFail);
    }
}
