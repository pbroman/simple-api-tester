package dev.pbroman.brat.core.records;

import dev.pbroman.brat.core.api.ConfigRecord;
import dev.pbroman.brat.core.records.result.Validation;
import dev.pbroman.brat.core.records.runtime.RuntimeData;
import dev.pbroman.brat.core.util.Interpolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record ResponseHandling(List<Condition> assertions, Map<String, String> setVars) implements ConfigRecord {

    public ResponseHandling {
        if (assertions == null) {
            assertions = List.of();
        }
        if (setVars == null) {
            setVars = Map.of();
        }
    }

    @Override
    public List<Validation> validate() {
        var validations = new ArrayList<Validation>();
        for (var assertion : assertions) {
            validations.addAll(assertion.validate());
        }
        return validations;
    }

    @Override
    public ResponseHandling interpolated(RuntimeData runtimeData) {
        var interpolatedAssertions = assertions.stream()
                .map(condition -> condition.interpolated(runtimeData))
                .toList();

        return new ResponseHandling(
                interpolatedAssertions,
                Interpolation.interpolateMap(setVars, runtimeData)
        );

    }
}
