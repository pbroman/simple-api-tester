package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.api.ConfigRecord;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

import java.util.ArrayList;
import java.util.List;

public record FlowControl(Integer waitAfter, RepeatUntil repeatUntil) implements ConfigRecord {

    @Override
    public List<Validation> validate() {
        var validations = new ArrayList<Validation>();
        if (repeatUntil != null) {
            validations.addAll(repeatUntil.validate());
        }
        return validations;
    }

    @Override
    public FlowControl interpolated(RuntimeData runtimeData) {
        var interpolatedWaitAfter = (Integer) Interpolation.interpolate(waitAfter, runtimeData);
        var interpolatedRepeatUntil = repeatUntil.interpolated(runtimeData);
        return new FlowControl(interpolatedWaitAfter, interpolatedRepeatUntil);
    }
}
