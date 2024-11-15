package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.ConfigLoader;
import dev.pbroman.simple.api.tester.api.ConfigProcessor;
import dev.pbroman.simple.api.tester.api.TestSuiteRunner;
import dev.pbroman.simple.api.tester.records.Metadata;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.result.ValidationType;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import static dev.pbroman.simple.api.tester.util.Constants.VALIDATION_LOGGER;

/**
 * The @ConfigProcessor runs through the entire test suite, setting inherited values where necessary,
 * validating the configuration, and resolves constants and env values set in the configuration.
 */
public class ValidatingConfigProcessor implements ConfigProcessor {

    private static final Logger validationLog = LoggerFactory.getLogger(VALIDATION_LOGGER);

    private final ConfigLoader configLoader;
    private final TestSuiteRunner validationTestSuiteRunner;

    public ValidatingConfigProcessor(ConfigLoader configLoader, TestSuiteRunner testSuiteRunner) {
        this.configLoader = configLoader;
        this.validationTestSuiteRunner = testSuiteRunner;
    }

    /**
     * Process the test suite configuration and environment configuration.
     *
     * @param testSuiteLocation the location of the testSuite yaml file
     * @param env a map of environment variables
     * @return a TestSuiteRuntime object with the processed test suite and runtime data
     * @throws IOException if the config files cannot be read
     */
    @Override
    public TestSuiteRuntime loadConfig(String testSuiteLocation, Map<String, String> env) {

        try {
            var testSuite = configLoader.loadTestSuite(testSuiteLocation);
            var runtimeData = new RuntimeData(testSuite.constants(), env);

            var testSuiteRuntime = new TestSuiteRuntime(testSuite, runtimeData);
            validationTestSuiteRunner.run(testSuiteRuntime);

            return testSuiteRuntime;
        } catch (IOException e) {
            validationLog.error(e.getMessage(), e);
        }
        return null;
    }

    private boolean isValid(RuntimeData runtimeData) {

        // TODO log validations?

        var fails = runtimeData.validations().stream().filter(validation -> validation.validationType().equals(ValidationType.FAIL)).toList();
        var warns = runtimeData.validations().stream().filter(validation -> validation.validationType().equals(ValidationType.WARN)).toList();

        validationLog.info("Validations: {} WARN, {} FAIL", warns.size(), fails.size());

        return fails.isEmpty();
    }


    private void logValidation(Metadata metadata, Validation validation) {
        var prefix = metadata != null ? metadata.name() + ": " : "Anonymous: ";
        switch (validation.validationType()) {
            case WARN -> validationLog.warn(prefix + validation);
            case FAIL -> validationLog.error(prefix + validation);
        }
    }

}
