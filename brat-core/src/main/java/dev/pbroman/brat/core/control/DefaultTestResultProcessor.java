package dev.pbroman.brat.core.control;

import dev.pbroman.brat.core.api.MessageRenderer;
import dev.pbroman.brat.core.api.TestResultProcessor;
import dev.pbroman.brat.core.records.result.RequestResult;
import dev.pbroman.brat.core.records.runtime.RuntimeData;

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
