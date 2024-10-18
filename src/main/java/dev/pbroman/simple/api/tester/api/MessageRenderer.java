package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.result.AssertionResult;
import dev.pbroman.simple.api.tester.records.result.RequestResult;

public interface MessageRenderer {

    String renderTestResultMessage(RequestResult requestResult);

    String renderAssertionResultMessage(AssertionResult assertionResult);
}
