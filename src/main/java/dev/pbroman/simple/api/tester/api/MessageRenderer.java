package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.result.AssertionResult;
import dev.pbroman.simple.api.tester.records.result.TestResult;

public interface MessageRenderer {

    String renderTestResultMessage(TestResult testResult);

    String renderAssertionResultMessage(AssertionResult assertionResult);
}
