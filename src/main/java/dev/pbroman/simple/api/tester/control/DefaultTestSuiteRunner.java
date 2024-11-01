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
        var runtimeData = testSuiteRuntime.runtimeData();

        if (testSuite.subSuites() != null) {
            testSuite.subSuites().forEach(subSuite -> {
                subSuite.validate();
                run(new TestSuiteRuntime(collectionInheritance(subSuite, testSuite), runtimeData));
            });
        }

        if (testSuite.requests() != null) {
            testSuite.requests().forEach(request -> {
                request.validate();
                requestProcessor.processRequest(requestInheritance(request, testSuite), runtimeData);
            });
        }

    }
}
