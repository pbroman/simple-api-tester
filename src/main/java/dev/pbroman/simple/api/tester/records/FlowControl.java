package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

public record FlowControl(
        Integer waitAfter,
        RepeatUntil repeatUntil
    ) {

    public FlowControl interpolated(RuntimeData runtimeData) {
        var interpolatedWaitAfter = (Integer) Interpolation.interpolate(waitAfter, runtimeData);
        var interpolatedRepeatUntil = repeatUntil.interpolated(runtimeData);
        return new FlowControl(interpolatedWaitAfter, interpolatedRepeatUntil);
    }
}
