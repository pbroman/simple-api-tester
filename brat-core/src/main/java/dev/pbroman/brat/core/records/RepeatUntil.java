package dev.pbroman.brat.core.records;

import dev.pbroman.brat.core.api.ConfigRecord;
import dev.pbroman.brat.core.records.result.Validation;
import dev.pbroman.brat.core.records.result.ValidationType;
import dev.pbroman.brat.core.records.runtime.RuntimeData;
import dev.pbroman.brat.core.util.Interpolation;

import java.util.ArrayList;
import java.util.List;

import static dev.pbroman.brat.core.util.Constants.DEFAULT_MAX_ATTEMPTS;

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
