package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.api.TestSuiteRunner;
import dev.pbroman.simple.api.tester.records.TestSuite;
import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;

import static dev.pbroman.simple.api.tester.util.Constants.PATH_DELIMITER;
import static dev.pbroman.simple.api.tester.util.Inheritance.collectionInheritance;
import static dev.pbroman.simple.api.tester.util.Inheritance.requestInheritance;

public class DefaultTestSuiteRunner implements TestSuiteRunner {

    private final RequestProcessor requestProcessor;

    public DefaultTestSuiteRunner(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public void run(TestSuiteRuntime testSuiteRuntime) {
        runInternal(testSuiteRuntime, extendPath(testSuiteRuntime.testSuite(), null));
    }

    private void runInternal(TestSuiteRuntime testSuiteRuntime, String path) {

        var testSuite = testSuiteRuntime.testSuite();
        var runtimeData = testSuiteRuntime.runtimeData().withCurrentPath(path);

        if (testSuite.subSuites() != null) {
            testSuite.subSuites().forEach(subSuite -> {
                runtimeData.validations().addAll(subSuite.validate());
                var currentPath = extendPath(subSuite, path);
                runInternal(new TestSuiteRuntime(collectionInheritance(subSuite, testSuite), runtimeData.withCurrentPath(currentPath)), currentPath);
            });
        }

        if (testSuite.requests() != null) {
            testSuite.requests().forEach(request -> {
                runtimeData.validations().addAll(request.validate());
                requestProcessor.processRequest(requestInheritance(request, testSuite), runtimeData.incrementRequestNo());
            });
        }

    }

    private String extendPath(TestSuite testSuite, String path) {
        var testSuiteName = testSuite.metadata() != null && testSuite.metadata().name() != null ? testSuite.metadata().name() : "UnnamedSuite";
        return path == null ? testSuiteName : String.join(PATH_DELIMITER, path, testSuiteName);
    }
}
