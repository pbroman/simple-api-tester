package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.api.SingleRequestRunner;
import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;

import static dev.pbroman.simple.api.tester.util.Inheritance.collectionInheritance;
import static dev.pbroman.simple.api.tester.util.Inheritance.requestInheritance;

public class DefaultSingleRequestRunner implements SingleRequestRunner {

    private final RequestProcessor requestProcessor;

    public DefaultSingleRequestRunner(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    @Override
    public void run(TestSuiteRuntime testSuiteRuntime, String requestIdentifier) {

        if (requestIdentifier == null) {
            throw new IllegalArgumentException("Request identifier must not be null");
        }

        var testSuite = testSuiteRuntime.testSuite();
        var runtimeData = testSuiteRuntime.runtimeData();

        if (testSuite.subSuites() != null) {
            testSuite.subSuites().forEach(subSuite -> {
                runtimeData.validations().addAll(subSuite.validate());
                run(new TestSuiteRuntime(collectionInheritance(subSuite, testSuite), runtimeData), requestIdentifier);
            });
        }

        if (testSuite.requests() != null) {
            testSuite.requests().forEach(request -> {
                if (requestIdentifier.equals(request.id()) || (request.metadata() != null && requestIdentifier.equals(request.metadata().name())) ) {
                    runtimeData.validations().addAll(request.validate());
                    requestProcessor.processRequest(requestInheritance(request, testSuite), runtimeData.incrementRequestNo());
                }
            });
        }
    }
}
