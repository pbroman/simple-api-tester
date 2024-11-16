package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;

public interface SingleRequestRunner {

    void run(TestSuiteRuntime testSuiteRuntime, String requestIdentifier);
}
