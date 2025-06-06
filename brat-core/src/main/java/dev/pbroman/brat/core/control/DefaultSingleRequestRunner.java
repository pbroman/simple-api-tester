package dev.pbroman.brat.core.control;

import dev.pbroman.brat.core.api.RequestProcessor;
import dev.pbroman.brat.core.api.SingleRequestRunner;
import dev.pbroman.brat.core.records.runtime.TestSuiteRuntime;

import static dev.pbroman.brat.core.util.Inheritance.collectionInheritance;
import static dev.pbroman.brat.core.util.Inheritance.requestInheritance;

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
