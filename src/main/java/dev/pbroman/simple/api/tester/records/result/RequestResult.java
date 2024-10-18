package dev.pbroman.simple.api.tester.records.result;

import java.util.List;

public record RequestResult(
        Object response,
        int numAttempt,
        long roundTripTime,
        List<AssertionResult> assertionResults) {

    public RequestResult {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        if (assertionResults == null) {
            throw new IllegalArgumentException("Assertion results cannot be null");
        }
    }

}
