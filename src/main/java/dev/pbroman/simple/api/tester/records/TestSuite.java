package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.api.ConfigRecord;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.result.ValidationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.pbroman.simple.api.tester.util.Constants.DEFAULT_TIMEOUT_MS;


public record TestSuite(
        Metadata metadata,
        Auth auth,
        Condition skipCondition,
        Map<String, String> constants,
        String defaultTimeout,
        List<Request> requests,
        List<TestSuite> subSuites,
        List<Validation> validations
    ) implements ConfigRecord {

    public TestSuite {
        if (auth == null) {
            auth = new Auth(Auth.AUTH_TYPE_NONE, null, null, null);
        }
        if (defaultTimeout == null) {
            defaultTimeout = DEFAULT_TIMEOUT_MS;
        }
        if (validations == null) {
            validations = new ArrayList<>();
        }
        if (requests == null) {
            requests = List.of();
        }
        if (subSuites == null) {
            subSuites = List.of();
        }
    }

    @Override
    public List<Validation> validate() {
        if (metadata == null) {
            validations.add(validation("The test suite metadata cannot be null", ValidationType.FAIL));
        } else {
            validations.addAll(metadata.validate());
        }
        if (requests.isEmpty() && subSuites.isEmpty()) {
            validations.add(validation("A test suite should have at least one request or subsuite. This suite is empty", ValidationType.WARN));
        }

        if (skipCondition != null) {
            validations.addAll(skipCondition.validate());
        }
        if (auth != null) {
            validations.addAll(auth.validate());
        }

        return validations;
    }

    private Validation validation(String message, ValidationType validationType) {
        var instanceName = metadata != null ? metadata.name() : null;
        return new Validation(this.getClass().getSimpleName(), instanceName, message, validationType);
    }


    public TestSuite withAuth(Auth auth) {
        return new TestSuite(metadata, auth, skipCondition, constants, defaultTimeout, requests, subSuites, validations);
    }
    public TestSuite withTimeout(String timeout) {
        return new TestSuite(metadata, auth,  skipCondition,constants, timeout, requests, subSuites, validations);
    }
}
