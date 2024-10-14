package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.records.TestSuite;
import dev.pbroman.simple.api.tester.records.result.TestResult;
import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static dev.pbroman.simple.api.tester.util.Constants.PROTOCOL_LOGGER;
import static dev.pbroman.simple.api.tester.util.Inheritance.collectionInheritance;
import static dev.pbroman.simple.api.tester.util.Inheritance.requestInheritance;

public class TestSuiteRunner {

    private static final Logger protocol = LoggerFactory.getLogger(PROTOCOL_LOGGER);

    private final RequestProcessor requestProcessor;

    public TestSuiteRunner(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public Map<TestSuite, List<Object>> run(TestSuiteRuntime testSuiteRuntime) {

        var testSuite = testSuiteRuntime.testSuite();
        var runtimeData = testSuiteRuntime.runtimeData();

        protocol.info("Test suite: {} ({})", testSuite.metadata().name(), testSuite.metadata().description());

        if (testSuite.subSuites() != null) {
            testSuite.subSuites().forEach(subSuite -> {
                run(new TestSuiteRuntime(collectionInheritance(subSuite, testSuite), runtimeData.withCurrentTestSuite(subSuite)));
            });
        }

        if (testSuite.requests() != null) {
            testSuite.requests().forEach(request -> {
                requestProcessor.processRequest(requestInheritance(request, testSuite), runtimeData);
            });
        }

        return runtimeData.results();
    }
}
