package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.ConfigLoader;
import dev.pbroman.simple.api.tester.api.ConfigProcessor;
import dev.pbroman.simple.api.tester.api.TestSuiteRunner;
import dev.pbroman.simple.api.tester.records.Metadata;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.result.ValidationType;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.records.TestSuite;
import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dev.pbroman.simple.api.tester.config.ApiTesterConfig.VALIDATION_STACK;
import static dev.pbroman.simple.api.tester.util.Constants.VALIDATION_LOGGER;

/**
 * The @ConfigProcessor runs through the entire test suite, setting inherited values where necessary,
 * validating the configuration, and resolves constants and env values set in the configuration.
 */
@Component
public class ValidatingConfigProcessor implements ConfigProcessor {

    private static final Logger validationLog = LoggerFactory.getLogger(VALIDATION_LOGGER);

    private final ConfigLoader configLoader;
    private final TestSuiteRunner validationTestSuiteRunner;

    public ValidatingConfigProcessor(ConfigLoader configLoader, @Qualifier(VALIDATION_STACK) TestSuiteRunner testSuiteRunner) {
        this.configLoader = configLoader;
        this.validationTestSuiteRunner = testSuiteRunner;
    }

    /**
     * Process the test suite configuration and environment configuration.
     *
     * @param testSuiteLocation the location of the testSuite yaml file
     * @param envLocation the location of the env yaml file
     * @return a TestSuiteRuntime object with the processed test suite and runtime data
     * @throws IOException if the config files cannot be read
     */
    @Override
    public TestSuiteRuntime loadConfig(String testSuiteLocation, String envLocation) {

        try {
            var testSuite = configLoader.loadTestSuite(testSuiteLocation);
            var env = envLocation != null ? configLoader.loadEnv(envLocation) : null;
            var runtimeData = new RuntimeData(testSuite.constants(), env);

            var testSuiteRuntime = new TestSuiteRuntime(testSuite, runtimeData);
            validationTestSuiteRunner.run(testSuiteRuntime);

            if (!isValid(testSuite)) {
                validationLog.error("Test suite is invalid");
            }
            return testSuiteRuntime;
        } catch (IOException e) {
            validationLog.error(e.getMessage(), e);
        }
        return null;
    }

    private boolean isValid(TestSuite testSuite) {
        var validations = new ArrayList<Validation>();
        collectAndLogValidations(testSuite, validations);
        testSuite.subSuites().forEach(subSuite -> {
            collectAndLogValidations(subSuite, validations);
        });

        var fails = validations.stream().filter(validation -> validation.validationType().equals(ValidationType.FAIL)).toList();
        var warns = validations.stream().filter(validation -> validation.validationType().equals(ValidationType.WARN)).toList();

        validationLog.info("Validations: {} WARN, {} FAIL", warns.size(), fails.size());

        return fails.isEmpty();
    }

    private void collectAndLogValidations(TestSuite testSuite, List<Validation> validations) {
        testSuite.validations().forEach(validation -> logValidation(testSuite.metadata(), validation));
        validations.addAll(testSuite.validations());
        testSuite.requests().forEach(request -> {
            request.validations().forEach(validation -> logValidation(request.metadata(), validation));
            validations.addAll(request.validations());
        });

    }

    private void logValidation(Metadata metadata, Validation validation) {
        var prefix = metadata != null ? metadata.name() + ": " : "Anonymous: ";
        switch (validation.validationType()) {
            case WARN -> validationLog.warn(prefix + validation);
            case FAIL -> validationLog.error(prefix + validation);
        }
    }

}
