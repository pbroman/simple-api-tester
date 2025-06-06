package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.result.AssertionResult;
import dev.pbroman.brat.core.records.result.RequestResult;

public interface MessageRenderer {

    String renderTestResultMessage(RequestResult requestResult);

    String renderAssertionResultMessage(AssertionResult assertionResult);
}
