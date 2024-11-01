package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;

public interface TestSuiteRunner {

    void run(TestSuiteRuntime testSuiteRuntime);
}
