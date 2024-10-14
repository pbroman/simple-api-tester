package dev.pbroman.simple.api.tester.records;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static dev.pbroman.simple.api.tester.util.Constants.DEFAULT_TIMEOUT_MS;


public record TestSuite(
        Metadata metadata,
        Auth auth,
        Condition skipCondition,
        Map<String, String> constants,
        String defaultTimeout,
        List<Request> requests,
        List<TestSuite> subSuites
    ) {

    public TestSuite {
        if (metadata == null) {
            throw new IllegalArgumentException("The test suite metadata cannot be null");
        }
        if (requests == null && subSuites == null) {
            throw new IllegalArgumentException("A test suite must have at least one request or subsuite");
        }
        if (defaultTimeout == null) {
            defaultTimeout = DEFAULT_TIMEOUT_MS;
        }
    }

    public TestSuite withAuth(Auth auth) {
        return new TestSuite(metadata, auth, skipCondition, constants, defaultTimeout, requests, subSuites);
    }
    public TestSuite withTimeout(String timeout) {
        return new TestSuite(metadata, auth,  skipCondition,constants, timeout, requests, subSuites);
    }
}
