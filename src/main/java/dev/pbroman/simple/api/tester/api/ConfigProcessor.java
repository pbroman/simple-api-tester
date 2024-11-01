package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;

import java.io.IOException;
import java.util.Map;

public interface ConfigProcessor {

    TestSuiteRuntime loadConfig(String testSuiteLocation, Map<String, String> env) throws IOException;
}
