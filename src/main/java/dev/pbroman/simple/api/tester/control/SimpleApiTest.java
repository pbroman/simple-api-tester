package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.ConfigProcessor;
import dev.pbroman.simple.api.tester.records.TestSuite;
import dev.pbroman.simple.api.tester.records.result.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SimpleApiTest {

    private static final Logger log = LoggerFactory.getLogger(SimpleApiTest.class);

    private ConfigProcessor configProcessor;
    private TestSuiteRunner testSuiteRunner;

    public SimpleApiTest(ConfigProcessor configProcessor, TestSuiteRunner testSuiteRunner) {
        this.configProcessor = configProcessor;
        this.testSuiteRunner = testSuiteRunner;
    }

    public Map<TestSuite, List<TestResult>> run(String testSuiteLocation, String envLocation) {

        try {
            var testSuiteRuntime = configProcessor.loadConfig(testSuiteLocation, envLocation);
            return testSuiteRunner.run(testSuiteRuntime);
        } catch (Exception e) {
            log.error("Error running test suite", e);
            return null;
        }
    }
}
