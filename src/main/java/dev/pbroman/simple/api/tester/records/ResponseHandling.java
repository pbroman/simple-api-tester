package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

import java.util.List;
import java.util.Map;

public record ResponseHandling(
        List<Condition> assertions,
        Map<String, String> setVars
    ) {

    public ResponseHandling {
        if (assertions == null) {
            assertions = List.of();
        }
        if (setVars == null) {
            setVars = Map.of();
        }
    }

    public ResponseHandling withTimeout(String timeout) {
        return new ResponseHandling(assertions, setVars);
    }

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
