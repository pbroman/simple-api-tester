package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.MessageRenderer;
import dev.pbroman.simple.api.tester.api.TestResultProcessor;
import dev.pbroman.simple.api.tester.records.result.TestResult;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import org.springframework.stereotype.Component;

@Component
public class DefaultTestResultProcessor implements TestResultProcessor {

    private MessageRenderer messageRenderer;

    public DefaultTestResultProcessor(MessageRenderer messageRenderer) {
        this.messageRenderer = messageRenderer;
    }

    @Override
    public void process(TestResult testResult, RuntimeData runtimeData) {

        runtimeData.addTestResult(testResult);

        System.out.println(messageRenderer.renderTestResultMessage(testResult));
        testResult.assertionResults().forEach( assertion -> {
            if (assertion.passed()) {
                System.out.println("  " + messageRenderer.renderAssertionResultMessage(assertion));
            } else {
                System.err.println("  " + messageRenderer.renderAssertionResultMessage(assertion));
            }
        });
    }
}
