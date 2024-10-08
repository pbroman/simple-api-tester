package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.result.TestResult;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;

public interface TestResultProcessor {

    void process(TestResult testResult, RuntimeData runtimeData);
}
