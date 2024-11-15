package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.HttpRequestHandler;
import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.api.ResponseHandler;
import dev.pbroman.simple.api.tester.api.TestResultProcessor;
import dev.pbroman.simple.api.tester.exception.ValidationException;
import dev.pbroman.simple.api.tester.records.result.RequestResult;
import dev.pbroman.simple.api.tester.records.result.ValidationType;
import dev.pbroman.simple.api.tester.util.ConditionResolver;
import dev.pbroman.simple.api.tester.records.Request;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static dev.pbroman.simple.api.tester.util.Constants.PATH_DELIMITER;
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

        var path = String.join(PATH_DELIMITER, runtimeData.currentPath(), request.metadata().name());
        RequestResult requestResult;

        if (request.skipCondition() != null && ConditionResolver.resolve(request.skipCondition().interpolated(runtimeData))) {
//            protocol.info("Skipping request: '{}' due to skipCondition: {}", request.metadata().name(), request.skipCondition().message());
            requestResult = new RequestResult(request.requestDefinition().interpolated(runtimeData), path, runtimeData.currentRequestNo(), null, -1, 0, null);
        } else {
            requestResult = processInternal(request, runtimeData, path, 1);
        }

        testResultProcessor.process(requestResult, runtimeData);
    }

    private RequestResult processInternal(Request request, RuntimeData runtimeData, String path, int numAttempt) {

        var requestDefinition = request.requestDefinition().interpolated(runtimeData);
//        protocol.info("{}: {} {} ({}), body: {}", request.metadata().name(), requestDefinition.method(), requestDefinition.url(), request.metadata().description(), requestDefinition.body());
        try {
            new URI(requestDefinition.url());
        } catch (URISyntaxException e) {
            throw new ValidationException("Invalid URL: " + requestDefinition.url(), ValidationType.FAIL, e);
        }

        long startTime = System.currentTimeMillis();
        var response = httpRequestHandler.performRequest(requestDefinition);
        long roundTripTime = System.currentTimeMillis() - startTime;

//        protocol.info("  Response status: {}", response.getStatusCode());
//        protocol.info("  Response body: {}", response.getBody());

        runtimeData = runtimeData.withHttpResponseVars(response);
        var assertionResults = responseHandler.handleResponse(request.responseHandling(), runtimeData);
        var requestResult = new RequestResult(requestDefinition, path, runtimeData.currentRequestNo(), response, numAttempt, roundTripTime, assertionResults);

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
                    processInternal(request, runtimeData, path, numAttempt + 1);
                }
            }
        }

        return requestResult;
    }
}
