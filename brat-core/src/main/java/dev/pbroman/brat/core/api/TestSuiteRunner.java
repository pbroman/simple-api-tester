package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.runtime.TestSuiteRuntime;

public interface TestSuiteRunner {

    void run(TestSuiteRuntime testSuiteRuntime);
}
