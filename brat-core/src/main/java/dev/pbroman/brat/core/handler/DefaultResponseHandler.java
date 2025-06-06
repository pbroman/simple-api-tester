package dev.pbroman.brat.core.handler;

import dev.pbroman.brat.core.api.ResponseHandler;
import dev.pbroman.brat.core.records.result.AssertionResult;
import dev.pbroman.brat.core.util.ConditionResolver;
import dev.pbroman.brat.core.util.Interpolation;
import dev.pbroman.brat.core.records.ResponseHandling;
import dev.pbroman.brat.core.records.runtime.RuntimeData;

import java.util.ArrayList;
import java.util.List;


public class DefaultResponseHandler implements ResponseHandler {

    @Override
    public List<AssertionResult> handleResponse(ResponseHandling responseHandling, RuntimeData runtimeData) {

        var assertionResults = new ArrayList<AssertionResult>();

        responseHandling.assertions().forEach(condition -> {
            var interpolatedCondition = condition.interpolated(runtimeData);
            var passed = ConditionResolver.resolve(interpolatedCondition);
            assertionResults.add(new AssertionResult(condition, interpolatedCondition, passed));
        });

        responseHandling.setVars().forEach((key, value) -> {
            runtimeData.vars().put(key, Interpolation.interpolate(value, runtimeData));
        });

        return assertionResults;
    }
}
