package dev.pbroman.brat.core.records;

import dev.pbroman.brat.core.api.ConfigRecord;
import dev.pbroman.brat.core.records.result.Validation;
import dev.pbroman.brat.core.records.result.ValidationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.pbroman.brat.core.util.Constants.DEFAULT_TIMEOUT_MS;


public record TestSuite(
        Metadata metadata,
        Auth auth,
        Condition skipCondition,
        Map<String, String> constants,
        String defaultTimeout,
        List<Request> requests,
        List<TestSuite> subSuites
    ) implements ConfigRecord {

    public TestSuite {
        if (defaultTimeout == null) {
            defaultTimeout = DEFAULT_TIMEOUT_MS;
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
        var validations = new ArrayList<Validation>();

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
        var instanceName = metadata != null && metadata.name() != null ? metadata.name() : "Anonymous test suite";
        return new Validation(this.getClass().getSimpleName(), instanceName, message, validationType);
    }

    public TestSuite withAuth(Auth auth) {
        return new TestSuite(metadata, auth, skipCondition, constants, defaultTimeout, requests, subSuites);
    }
    public TestSuite withTimeout(String timeout) {
        return new TestSuite(metadata, auth,  skipCondition,constants, timeout, requests, subSuites);
    }
}
