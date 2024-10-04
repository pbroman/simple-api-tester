package dev.pbroman.simple.api.tester.util;

import dev.pbroman.simple.api.tester.records.Request;
import dev.pbroman.simple.api.tester.records.TestSuite;

import static dev.pbroman.simple.api.tester.util.Constants.DEFAULT_AUTH;
import static dev.pbroman.simple.api.tester.util.Constants.DEFAULT_TIMEOUT_MS;

public class Inheritance {

    public static TestSuite collectionInheritance(TestSuite testSuite, TestSuite parent) {
        if (testSuite.defaultTimeout() == null) {
            testSuite = testSuite.withTimeout(parent != null ? parent.defaultTimeout() : DEFAULT_TIMEOUT_MS);
        }
        if (testSuite.auth() == null) {
            testSuite = testSuite.withAuth(parent != null ? parent.auth() : DEFAULT_AUTH);
        }
        return testSuite;
    }

    public static Request requestInheritance(Request request, TestSuite parent) {
        if (request.requestDefinition().timeout() == null) {
            request = request.withRequestDefinition(request.requestDefinition().withTimeout(parent.defaultTimeout()));
        }
        if (request.requestDefinition().auth() == null) {
            request = request.withRequestDefinition(request.requestDefinition().withAuth(parent.auth()));
        }
        return request;
    }

}
