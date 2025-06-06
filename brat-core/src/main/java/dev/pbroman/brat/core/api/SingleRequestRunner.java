package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.runtime.TestSuiteRuntime;

public interface SingleRequestRunner {

    void run(TestSuiteRuntime testSuiteRuntime, String requestIdentifier);
}
