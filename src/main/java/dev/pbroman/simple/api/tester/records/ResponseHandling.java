package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.api.ConfigRecord;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

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
