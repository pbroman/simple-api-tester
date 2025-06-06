package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.runtime.TestSuiteRuntime;

import java.io.IOException;
import java.util.Map;

public interface ConfigProcessor {

    TestSuiteRuntime loadConfig(String testSuiteLocation, Map<String, String> env) throws IOException;
}
