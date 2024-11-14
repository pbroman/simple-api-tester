package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.api.TestSuiteRunner;
import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;

import static dev.pbroman.simple.api.tester.util.Inheritance.collectionInheritance;
import static dev.pbroman.simple.api.tester.util.Inheritance.requestInheritance;

public class DefaultTestSuiteRunner implements TestSuiteRunner {

    private final RequestProcessor requestProcessor;

    public DefaultTestSuiteRunner(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public void run(TestSuiteRuntime testSuiteRuntime) {

        var testSuite = testSuiteRuntime.testSuite();
        var testSuiteName = testSuite.metadata() != null && testSuite.metadata().name() != null ? testSuite.metadata().name() : "UnnamedSuite";
        var runtimeData = testSuiteRuntime.runtimeData()
                .withCurrentPath(testSuiteRuntime.runtimeData().currentPath() + "/" + testSuiteName);

        if (testSuite.subSuites() != null) {
            testSuite.subSuites().forEach(subSuite -> {
                runtimeData.validations().addAll(subSuite.validate());
                run(new TestSuiteRuntime(collectionInheritance(subSuite, testSuite), runtimeData));
            });
        }

        if (testSuite.requests() != null) {
            testSuite.requests().forEach(request -> {
                runtimeData.validations().addAll(request.validate());
                requestProcessor.processRequest(requestInheritance(request, testSuite), runtimeData.incrementRequestNo());
            });
        }

    }
}
