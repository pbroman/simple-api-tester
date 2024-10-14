package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

import java.util.Objects;

public record Condition(String operation, Object value, Object other, String message) {

    public Condition(String operation, Object value, Object other) {
        this(operation, value, other, null);
    }
    
    public Condition {
        if (operation == null) {
            throw new IllegalArgumentException("A condition operation cannot be null. " +
                    "If you want the operation null in the yaml config, you must put it in quotes: 'null' or \"null\", or use isNull");
        }
    }

    public Condition interpolated(RuntimeData runtimeData) {
        var interpolatedValue = Interpolation.interpolate(value, runtimeData);
        var interpolatedOther = Interpolation.interpolate(other, runtimeData);
        return new Condition(operation, interpolatedValue, interpolatedOther, message);
    }

}
