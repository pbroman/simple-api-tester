package dev.pbroman.brat.core.records;

import dev.pbroman.brat.core.api.ConfigRecord;
import dev.pbroman.brat.core.records.result.Validation;
import dev.pbroman.brat.core.records.runtime.RuntimeData;
import dev.pbroman.brat.core.util.Interpolation;

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
