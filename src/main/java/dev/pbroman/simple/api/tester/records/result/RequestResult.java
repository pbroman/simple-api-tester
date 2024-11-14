package dev.pbroman.simple.api.tester.records.result;

import dev.pbroman.simple.api.tester.records.Request;

import java.util.List;

public record RequestResult(
        Request request,
        String path,
        Object response,
        int numAttempt,
        long roundTripTime,
        List<AssertionResult> assertionResults) {

    public RequestResult {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        if (assertionResults == null) {
            throw new IllegalArgumentException("Assertion results cannot be null");
        }
    }

}
