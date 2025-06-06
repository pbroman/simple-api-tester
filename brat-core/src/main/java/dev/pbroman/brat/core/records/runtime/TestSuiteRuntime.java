package dev.pbroman.brat.core.records.runtime;

import dev.pbroman.brat.core.records.TestSuite;

public record TestSuiteRuntime(TestSuite testSuite, RuntimeData runtimeData) {

    public TestSuiteRuntime {
        if (testSuite == null) {
            throw new IllegalArgumentException("TestSuite cannot be null");
        }
        if (runtimeData == null) {
            throw new IllegalArgumentException("RuntimeData cannot be null");
        }
    }
}
