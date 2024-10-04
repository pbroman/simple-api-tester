package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.HttpRequestHandler;
import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.api.ResponseHandler;
import dev.pbroman.simple.api.tester.records.result.TestResult;
import dev.pbroman.simple.api.tester.util.ConditionResolver;
import dev.pbroman.simple.api.tester.records.Request;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static dev.pbroman.simple.api.tester.util.Constants.PROTOCOL_LOGGER;

@Component
public class DefaultRequestProcessor implements RequestProcessor {

    private static final Logger protocol = LoggerFactory.getLogger(PROTOCOL_LOGGER);

    private final HttpRequestHandler httpRequestHandler;
    private final ResponseHandler responseHandler;

    public DefaultRequestProcessor(HttpRequestHandler httpRequestHandler, ResponseHandler responseHandler) {
        this.httpRequestHandler = httpRequestHandler;
        this.responseHandler = responseHandler;
    }

    @Override
    public void processRequest(Request request, RuntimeData runtimeData) {

        if (request.skipCondition() != null && ConditionResolver.resolve(request.skipCondition().interpolated(runtimeData))) {
            protocol.info("Skipping request: '{}' due to skipCondition: {}", request.metadata().name(), request.skipCondition().message());
            return;
        }

        var testResult = processInternal(request, runtimeData, 1);
        runtimeData.addTestResult(testResult);
    }

    private TestResult processInternal(Request request, RuntimeData runtimeData, int numAttempt) {

        var requestDefinition = request.requestDefinition().interpolated(runtimeData);
        protocol.info("{}: {} {} ({})", request.metadata().name(), requestDefinition.method(), requestDefinition.url(), request.metadata().description());

        long startTime = System.currentTimeMillis();
        var response = httpRequestHandler.performRequest(requestDefinition);
        long executionTime = System.currentTimeMillis() - startTime;



        protocol.info("  Response status: {}", response.getStatusCode());

        runtimeData = runtimeData.withHttpResponseVars(response);
        var assertionResults = responseHandler.handleResponse(request.responseHandling(), runtimeData);
        var testResult = new TestResult(request.metadata(), numAttempt, requestDefinition, response, executionTime, assertionResults);

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
