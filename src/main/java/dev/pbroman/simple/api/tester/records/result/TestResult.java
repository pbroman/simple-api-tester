package dev.pbroman.simple.api.tester.records.result;

import dev.pbroman.simple.api.tester.records.Metadata;
import dev.pbroman.simple.api.tester.records.RequestDefinition;

import java.util.List;

public record TestResult(
        Metadata requestMetadata,
        int numAttempt,
        RequestDefinition requestDefinition,
        Object response,
        long executionTime,
        List<AssertionResult> assertionResults) {

    public TestResult {
        if (requestMetadata == null) {
            throw new IllegalArgumentException("Request metadata cannot be null");
        }
        if (requestDefinition == null) {
            throw new IllegalArgumentException("Request definition cannot be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        if (assertionResults == null) {
            throw new IllegalArgumentException("Assertion results cannot be null");
        }
    }

}
