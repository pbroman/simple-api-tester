package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.MessageRenderer;
import dev.pbroman.simple.api.tester.api.TestResultProcessor;
import dev.pbroman.simple.api.tester.records.result.RequestResult;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;

public class DefaultTestResultProcessor implements TestResultProcessor {

    private final MessageRenderer messageRenderer;

    public DefaultTestResultProcessor(MessageRenderer messageRenderer) {
        this.messageRenderer = messageRenderer;
    }

    @Override
    public void process(RequestResult requestResult, RuntimeData runtimeData) {

        runtimeData.requestResults().add(requestResult);

        System.out.println(messageRenderer.renderTestResultMessage(requestResult));
        requestResult.assertionResults().forEach(assertion -> {
            System.out.println("  " + messageRenderer.renderAssertionResultMessage(assertion));
        });
        System.out.println();
    }
}
