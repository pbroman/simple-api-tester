package dev.pbroman.simple.api.tester.handler;

import dev.pbroman.simple.api.tester.api.MessageRenderer;
import dev.pbroman.simple.api.tester.records.result.AssertionResult;
import dev.pbroman.simple.api.tester.records.result.TestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CheapMessageRenderer implements MessageRenderer {

    @Value("${simple-api-tester.test-result-template}")
    private String testResultTemplate;

    @Value("${simple-api-tester.assertion-result-template}")
    private String assertionResultTemplate;

    public String renderTestResultMessage(TestResult testResult) {
        var response = (ResponseEntity<?>) testResult.response();
        // Yeah, this is cheap. It is supposed to be cheap. :)
        return testResultTemplate
                .replaceAll("<request.name>", replacementSafe(testResult.requestMetadata().name()))
                .replaceAll("<request.url>", replacementSafe(testResult.requestDefinition().url()))
                .replaceAll("<request.method>", replacementSafe(testResult.requestDefinition().method()))
                .replaceAll("<request.authType>", replacementSafe(testResult.requestDefinition().auth().type()))
                .replaceAll("<response>", replacementSafe(response))
                .replaceAll("<response.status>", replacementSafe(response.getStatusCode()))
                .replaceAll("<response.statusCode>", replacementSafe(response.getStatusCode().value()))
                .replaceAll("<request.rtt>", replacementSafe(testResult.roundTripTime()))
                .replaceAll("<request.numAttempts?>", replacementSafe(testResult.numAttempt()))
            ;
    }

    @Override
    public String renderAssertionResultMessage(AssertionResult assertionResult) {
        return assertionResultTemplate
                .replaceAll("<condition.value>", replacementSafe(assertionResult.condition().value()))
                .replaceAll("<condition.operation>", replacementSafe(assertionResult.condition().operation()))
                .replaceAll("<condition.other>", replacementSafe(assertionResult.condition().other()))
                .replaceAll("<passed>", replacementSafe(assertionResult.passed()))
                .replaceAll("<interpolatedCondition.value>", replacementSafe(assertionResult.interpolatedCondition().value()))
                .replaceAll("<interpolatedCondition.other>", replacementSafe(assertionResult.interpolatedCondition().other()))
            ;
    }

    private String replacementSafe(Object value) {
        if (value == null) {
            return "null";
        }
        return value.toString().replaceAll("\\$", "");
    }
}
