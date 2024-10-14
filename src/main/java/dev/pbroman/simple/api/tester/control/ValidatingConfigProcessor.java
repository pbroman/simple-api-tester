package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.ConfigLoader;
import dev.pbroman.simple.api.tester.api.ConfigProcessor;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.records.TestSuite;
import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static dev.pbroman.simple.api.tester.config.ApiTesterConfig.VALIDATION_STACK;
import static dev.pbroman.simple.api.tester.control.ValidationRequestProcessor.VALIDATION_ERRORS;
import static dev.pbroman.simple.api.tester.util.Constants.VALIDATION_LOGGER;
import static dev.pbroman.simple.api.tester.util.Inheritance.collectionInheritance;
import static dev.pbroman.simple.api.tester.util.Inheritance.requestInheritance;

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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TestSuiteRuntime loadConfig(String testSuiteLocation, String envLocation) {

        try {
            var testSuite = configLoader.loadTestSuite(testSuiteLocation);
            var env = envLocation != null ? configLoader.loadEnv(envLocation) : null;
            var runtimeData = new RuntimeData(testSuite.constants(), env);

            var testSuiteRuntime = new TestSuiteRuntime(testSuite, runtimeData.withCurrentTestSuite(testSuite));
            validationTestSuiteRunner.run(testSuiteRuntime);
            if (testSuiteRuntime.runtimeData().vars().get(VALIDATION_ERRORS) instanceof List errors) {
                validationLog.error("There are validation errors:");
                errors.forEach( e -> validationLog.error(e.toString()));
                return null;
            }
            return testSuiteRuntime;
        } catch (IOException e) {
            validationLog.error(e.getMessage());
        }
        return null;
    }

    private TestSuiteRuntime validate(TestSuite testSuite, RuntimeData runtimeData) {

        if (testSuite.subSuites() != null) {
            testSuite.subSuites().forEach(subSuite -> {
                validate(collectionInheritance(subSuite, testSuite), runtimeData);
            });
        }

        if (testSuite.requests() != null) {
            testSuite.requests().forEach(request -> {
                requestInheritance(request, testSuite);
            });
        }

        return new TestSuiteRuntime(testSuite, runtimeData.withCurrentTestSuite(testSuite));
    }

}
