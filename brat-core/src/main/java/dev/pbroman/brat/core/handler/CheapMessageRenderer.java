package dev.pbroman.brat.core.handler;

import dev.pbroman.brat.core.api.MessageRenderer;
import dev.pbroman.brat.core.records.result.AssertionResult;
import dev.pbroman.brat.core.records.result.RequestResult;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static dev.pbroman.brat.core.util.Constants.COLOR_BLACK_BOLD;
import static dev.pbroman.brat.core.util.Constants.COLOR_BLUE;
import static dev.pbroman.brat.core.util.Constants.COLOR_GREEN;
import static dev.pbroman.brat.core.util.Constants.COLOR_RED;
import static dev.pbroman.brat.core.util.Constants.COLOR_RESET;
import static dev.pbroman.brat.core.util.Constants.COLOR_YELLOW;
import static dev.pbroman.brat.core.util.Constants.PATH_DELIMITER;

public class CheapMessageRenderer implements MessageRenderer {

    public String renderTestResultMessage(RequestResult requestResult) {
        var response = (ResponseEntity<?>) requestResult.response();

        return colorize(requestResult.path().replace(PATH_DELIMITER, " / "), COLOR_BLACK_BOLD) + "\n"
                + colorizeMethod(requestResult.requestDefinition().method()) + " " + requestResult.requestDefinition().url() + "\n"
                + "Response: " + colorizeStatusCode(response.getStatusCode())
                + ", RTT: " + requestResult.roundTripTime() + " ms"
                + ", NumAttempts: " + requestResult.numAttempt() + "\n";
    }

    @Override
    public String renderAssertionResultMessage(AssertionResult assertionResult) {
        return (assertionResult.passed() ? colorize("PASS ", COLOR_GREEN) : colorize("FAIL ", COLOR_RED))
                + assertionResult.interpolatedCondition().message();
    }

    private String colorize(String message, String color) {
        return color + message + COLOR_RESET;
    }

    private String colorizeMethod(String method) {
        return switch (method) {
            case "GET" -> colorize(method, COLOR_GREEN);
            case "POST" -> colorize(method, COLOR_YELLOW);
            case "PUT" -> colorize(method, COLOR_BLUE);
            case "DELETE" -> colorize(method, COLOR_RED);
            default -> method;
        };
    }

    private String colorizeStatusCode(HttpStatusCode statusCode) {
        if (statusCode.is2xxSuccessful()) {
            return colorize(statusCode.toString(), COLOR_GREEN);
        } else if (statusCode.is3xxRedirection()) {
            return colorize(statusCode.toString(), COLOR_YELLOW);
        } else if (statusCode.is4xxClientError()) {
            return colorize(statusCode.toString(), COLOR_BLUE);
        } else if (statusCode.is5xxServerError()) {
            return colorize(statusCode.toString(), COLOR_RED);
        }
        return statusCode.toString();
    }

}
