package dev.pbroman.brat.core.records;

import dev.pbroman.brat.core.api.ConfigRecord;
import dev.pbroman.brat.core.records.result.Validation;
import dev.pbroman.brat.core.records.result.ValidationType;
import dev.pbroman.brat.core.records.runtime.RuntimeData;
import dev.pbroman.brat.core.util.Interpolation;

import java.util.ArrayList;
import java.util.List;

public record Condition(String operation, Object value, Object other, String message) implements ConfigRecord {

    public Condition(String operation, Object value, Object other) {
        this(operation, value, other, null);
    }


    @Override
    public List<Validation> validate() {
        var validations = new ArrayList<Validation>();
        if (operation == null) {
            var message = "A condition operation cannot be null. " +
                    "If you want the operation null in the yaml config, you must put it in quotes: 'null' or \"null\", or use isNull";
            validations.add(new Validation(this.getClass().getSimpleName(), getInstanceName(), message, ValidationType.FAIL));
        }
        return validations;
    }

    public String getInstanceName() {
        return this.message != null ? this.message : value + " " + operation + " " + (other != null ? other : "");
    }

    @Override
    public Condition interpolated(RuntimeData runtimeData) {
        var interpolatedValue = Interpolation.interpolate(value, runtimeData);
        var interpolatedOther = Interpolation.interpolate(other, runtimeData);
        return new Condition(operation, interpolatedValue, interpolatedOther, message);
    }

}
