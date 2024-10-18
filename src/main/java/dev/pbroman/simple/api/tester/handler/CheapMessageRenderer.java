package dev.pbroman.simple.api.tester.handler;

import dev.pbroman.simple.api.tester.api.MessageRenderer;
import dev.pbroman.simple.api.tester.records.result.AssertionResult;
import dev.pbroman.simple.api.tester.records.result.RequestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CheapMessageRenderer implements MessageRenderer {

    @Value("${simple-api-tester.test-result-template}")
    private String testResultTemplate;

    @Value("${simple-api-tester.assertion-result-template}")
    private String assertionResultTemplate;

    public String renderTestResultMessage(RequestResult requestResult) {
        var response = (ResponseEntity<?>) requestResult.response();
        // Yeah, this is cheap. It is supposed to be cheap. :)
        return testResultTemplate
//                .replaceAll("<request.name>", replacementSafe(requestResult.requestMetadata().name()))
//                .replaceAll("<request.url>", replacementSafe(requestResult.requestDefinition().url()))
//                .replaceAll("<request.method>", replacementSafe(requestResult.requestDefinition().method()))
//                .replaceAll("<request.authType>", replacementSafe(requestResult.requestDefinition().auth().type()))
                .replaceAll("<response>", replacementSafe(response))
                .replaceAll("<response.status>", replacementSafe(response.getStatusCode()))
                .replaceAll("<response.statusCode>", replacementSafe(response.getStatusCode().value()))
                .replaceAll("<request.rtt>", replacementSafe(requestResult.roundTripTime()))
                .replaceAll("<request.numAttempts?>", replacementSafe(requestResult.numAttempt()))
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
