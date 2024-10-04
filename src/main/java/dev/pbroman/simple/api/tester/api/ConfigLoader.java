package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.TestSuite;

import java.io.IOException;
import java.util.Map;

public interface ConfigLoader {

    TestSuite loadTestSuite(String location) throws IOException;

    Map<String, String> loadEnv(String location) throws IOException;
}
