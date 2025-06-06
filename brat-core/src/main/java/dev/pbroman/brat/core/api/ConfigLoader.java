package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.TestSuite;

import java.io.IOException;
import java.util.Map;

public interface ConfigLoader {

    TestSuite loadTestSuite(String location) throws IOException;

    Map<String, String> loadEnv(String location) throws IOException;
}
