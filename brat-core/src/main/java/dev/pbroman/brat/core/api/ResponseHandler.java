package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.ResponseHandling;
import dev.pbroman.brat.core.records.result.AssertionResult;
import dev.pbroman.brat.core.records.runtime.RuntimeData;

import java.util.List;

public interface ResponseHandler {

    List<AssertionResult> handleResponse(ResponseHandling responseHandling, RuntimeData runtimeData);
}
