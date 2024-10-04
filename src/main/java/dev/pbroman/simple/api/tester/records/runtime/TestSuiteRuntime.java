package dev.pbroman.simple.api.tester.records.runtime;

import dev.pbroman.simple.api.tester.records.TestSuite;

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
