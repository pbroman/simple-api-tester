package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.result.RequestResult;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;

public interface TestResultProcessor {

    void process(RequestResult requestResult, RuntimeData runtimeData);
}
