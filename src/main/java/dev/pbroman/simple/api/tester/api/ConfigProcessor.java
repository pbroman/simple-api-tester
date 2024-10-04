package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;

import java.io.IOException;

public interface ConfigProcessor {

    TestSuiteRuntime loadConfig(String testSuiteLocation, String envLocation) throws IOException;
}
