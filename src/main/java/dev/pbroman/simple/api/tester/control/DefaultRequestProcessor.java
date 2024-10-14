package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.HttpRequestHandler;
import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.api.ResponseHandler;
import dev.pbroman.simple.api.tester.api.TestResultProcessor;
import dev.pbroman.simple.api.tester.records.result.TestResult;
import dev.pbroman.simple.api.tester.util.ConditionResolver;
import dev.pbroman.simple.api.tester.records.Request;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static dev.pbroman.simple.api.tester.util.Constants.PROTOCOL_LOGGER;

public class DefaultRequestProcessor implements RequestProcessor {

    private static final Logger protocol = LoggerFactory.getLogger(PROTOCOL_LOGGER);

    private final HttpRequestHandler httpRequestHandler;
    private final ResponseHandler responseHandler;
    private final TestResultProcessor testResultProcessor;

    public DefaultRequestProcessor(HttpRequestHandler httpRequestHandler, ResponseHandler responseHandler, TestResultProcessor testResultProcessor) {
        this.httpRequestHandler = httpRequestHandler;
        this.responseHandler = responseHandler;
        this.testResultProcessor = testResultProcessor;
    }

    @Override
    public void processRequest(Request request, RuntimeData runtimeData) {

        if (request.skipCondition() != null && ConditionResolver.resolve(request.skipCondition().interpolated(runtimeData))) {
            protocol.info("Skipping request: '{}' due to skipCondition: {}", request.metadata().name(), request.skipCondition().message());
            return;
        }

        var testResult = processInternal(request, runtimeData, 1);
        testResultProcessor.process(testResult, runtimeData);
    }

    private TestResult processInternal(Request request, RuntimeData runtimeData, int numAttempt) {

        var requestDefinition = request.requestDefinition().interpolated(runtimeData);
        protocol.info("{}: {} {} ({})", request.metadata().name(), requestDefinition.method(), requestDefinition.url(), request.metadata().description());

        long startTime = System.currentTimeMillis();
        var response = httpRequestHandler.performRequest(requestDefinition);
        long roundTripTime = System.currentTimeMillis() - startTime;

        protocol.info("  Response status: {}", response.getStatusCode());
        protocol.info("  Response body: {}", response.getBody());

        runtimeData = runtimeData.withHttpResponseVars(response);
        var assertionResults = responseHandler.handleResponse(request.responseHandling(), runtimeData);
        var testResult = new TestResult(request.metadata(), requestDefinition, response, numAttempt, roundTripTime, assertionResults);

        /*
         * Flow control, using data from the response
         */
        if (request.flowControl() != null) {
            var flowControl = request.flowControl().interpolated(runtimeData);

            // Wait after the request (if set)
            if (flowControl.waitAfter() != null) {
                Awaitility.await().atMost(flowControl.waitAfter(), TimeUnit.MILLISECONDS)
                        .until(() -> true);
            }

            // Repeat until the condition is met (if set)
            if (flowControl.repeatUntil() != null) {
                var repeatUntil = flowControl.repeatUntil();
                while (!ConditionResolver.resolve(repeatUntil.condition()) && numAttempt < repeatUntil.maxAttempts()) {
                    Awaitility.await().atMost(flowControl.repeatUntil().waitBetweenAttempts(), TimeUnit.MILLISECONDS)
                            .until(() -> true);
                    processInternal(request, runtimeData, numAttempt + 1);
                }
            }
        }

        return testResult;
    }
}
