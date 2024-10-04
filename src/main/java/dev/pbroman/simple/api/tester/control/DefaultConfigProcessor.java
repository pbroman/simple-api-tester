package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.ConfigLoader;
import dev.pbroman.simple.api.tester.api.ConfigProcessor;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.records.TestSuite;
import dev.pbroman.simple.api.tester.records.runtime.TestSuiteRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static dev.pbroman.simple.api.tester.util.Inheritance.collectionInheritance;
import static dev.pbroman.simple.api.tester.util.Inheritance.requestInheritance;

/**
 * The @ConfigProcessor runs through the entire test suite, setting inherited values where necessary,
 * validating the configuration, and resolves constants and env values set in the configuration.
 */
@Component
public class DefaultConfigProcessor implements ConfigProcessor {

    private static final Logger log = LoggerFactory.getLogger(DefaultConfigProcessor.class);

    private final ConfigLoader configLoader;

    public DefaultConfigProcessor(ConfigLoader configLoader) {
        this.configLoader = configLoader;
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
    public TestSuiteRuntime loadConfig(String testSuiteLocation, String envLocation) throws IOException {

        var testSuite = configLoader.loadTestSuite(testSuiteLocation);
        var env = envLocation != null ? configLoader.loadEnv(envLocation) : null;
        var runtimeData = new RuntimeData(testSuite.constants(), env);

        return validate(testSuite, runtimeData);
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
