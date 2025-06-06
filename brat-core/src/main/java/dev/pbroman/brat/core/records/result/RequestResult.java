package dev.pbroman.brat.core.records.result;

import dev.pbroman.brat.core.records.RequestDefinition;

import java.util.List;

public record RequestResult(
        RequestDefinition requestDefinition,
        String path,
        int requestNo,
        Object response,
        int numAttempt,
        long roundTripTime,
        List<AssertionResult> assertionResults) {

    public RequestResult {
        if (requestDefinition == null) {
            throw new IllegalArgumentException("RequestDefinition cannot be null");
        }
        if (assertionResults == null) {
            assertionResults = List.of();
        }
    }

}
