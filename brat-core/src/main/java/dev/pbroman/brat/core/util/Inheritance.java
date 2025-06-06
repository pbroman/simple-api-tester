package dev.pbroman.brat.core.util;

import dev.pbroman.brat.core.records.Request;
import dev.pbroman.brat.core.records.TestSuite;

import static dev.pbroman.brat.core.util.Constants.AUTH_NONE;
import static dev.pbroman.brat.core.util.Constants.DEFAULT_TIMEOUT_MS;

public class Inheritance {

    public static TestSuite collectionInheritance(TestSuite testSuite, TestSuite parent) {
        if (parent == null) {
            return testSuite;
        }
        if (testSuite.defaultTimeout() == null) {
            testSuite = testSuite.withTimeout(parent.defaultTimeout() != null ? parent.defaultTimeout() : DEFAULT_TIMEOUT_MS);
        }
        if (testSuite.auth() == null) {
            testSuite = testSuite.withAuth(parent.auth() != null ? parent.auth() : AUTH_NONE);
        }
        return testSuite;
    }

    public static Request requestInheritance(Request request, TestSuite parent) {
        if (parent == null) {
            return request;
        }
        if (request.requestDefinition().timeout() == null) {
            var timeout = parent.defaultTimeout() != null ? parent.defaultTimeout() : DEFAULT_TIMEOUT_MS;
            request = request.withRequestDefinition(request.requestDefinition().withTimeout(timeout));
        }
        if (request.requestDefinition().auth() == null) {
            var auth = parent.auth() != null ? parent.auth() : AUTH_NONE;
            request = request.withRequestDefinition(request.requestDefinition().withAuth(auth));
        }
        return request;
    }

}
