package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.ResponseHandling;
import dev.pbroman.simple.api.tester.records.result.AssertionResult;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;

import java.util.List;

public interface ResponseHandler {

    List<AssertionResult> handleResponse(ResponseHandling responseHandling, RuntimeData runtimeData);
}
