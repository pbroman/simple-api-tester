package dev.pbroman.simple.api.tester.handler;

import dev.pbroman.simple.api.tester.api.MessageRenderer;
import dev.pbroman.simple.api.tester.records.result.AssertionResult;
import dev.pbroman.simple.api.tester.records.result.RequestResult;
import org.springframework.http.ResponseEntity;

import static dev.pbroman.simple.api.tester.util.Constants.COLOR_BLACK_BOLD;
import static dev.pbroman.simple.api.tester.util.Constants.COLOR_GREEN;
import static dev.pbroman.simple.api.tester.util.Constants.COLOR_RED;
import static dev.pbroman.simple.api.tester.util.Constants.COLOR_RESET;
import static dev.pbroman.simple.api.tester.util.Constants.PATH_DELIMITER;

public class CheapMessageRenderer implements MessageRenderer {

    public String renderTestResultMessage(RequestResult requestResult) {
        var response = (ResponseEntity<?>) requestResult.response();

        return COLOR_BLACK_BOLD + requestResult.path().replace(PATH_DELIMITER, " / ") + COLOR_RESET + "\n"
                + requestResult.requestDefinition().method() + " " + requestResult.requestDefinition().url() + "\n"
                + "Response: " + response.getStatusCode() + ", RTT: " + requestResult.roundTripTime() + " ms"
                + ", NumAttempts: " + requestResult.numAttempt() + "\n";
    }

    @Override
    public String renderAssertionResultMessage(AssertionResult assertionResult) {
        return (assertionResult.passed() ? COLOR_GREEN + "PASS " : COLOR_RED + "FAIL ") + COLOR_RESET + assertionResult.interpolatedCondition().message();
    }

}
