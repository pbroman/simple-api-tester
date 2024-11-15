package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.ConfigProcessor;
import dev.pbroman.simple.api.tester.api.TestSuiteRunner;
import dev.pbroman.simple.api.tester.config.ApiTesterAutoConfiguration;
import dev.pbroman.simple.api.tester.records.TestSuite;
import dev.pbroman.simple.api.tester.records.result.AssertionResult;
import dev.pbroman.simple.api.tester.records.result.RequestResult;
import dev.pbroman.simple.api.tester.testapp.CrudApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(classes = {ApiTesterAutoConfiguration.class, JacksonAutoConfiguration.class})
class TestSuiteRunnerTest {

    @Autowired
    private ConfigProcessor configProcessor;

    @Autowired
    private TestSuiteRunner testSuiteRunner;

    private static ConfigurableApplicationContext context;

    @BeforeAll
    static void init() {
        System.out.println("========== Starting CrudApplication");
        context = SpringApplication.run(CrudApplication.class);
    }

    @AfterAll
    static void close() {
        System.out.println("========== Closing CrudApplication");
        context.close();
    }

    @Test
    void run() throws IOException {
        // given
        var env = Map.of("baseUrl", "http://localhost:8080");
        var testSuiteRuntime = configProcessor.loadConfig("classpath:crudApiSpec.yaml", env);

        // when
        testSuiteRunner.run(testSuiteRuntime);

        // then
        assertNotNull(testSuiteRuntime.runtimeData().validations());
        var requestResults = collectRequestResults(testSuiteRuntime.testSuite(), new ArrayList<>());

        assertNotNull(requestResults);

        requestResults.stream().flatMap(requestResult -> requestResult.assertionResults().stream())
                .filter( result -> !result.passed())
                .forEach(System.out::println);

        assertTrue(requestResults.stream().flatMap(requestResult -> requestResult.assertionResults().stream())
                .allMatch(AssertionResult::passed));


    }

    private List<RequestResult> collectRequestResults(TestSuite testSuite, List<RequestResult> previousResults) {
        // TODO get from runtimeData
        testSuite.subSuites().forEach(subSuite -> collectRequestResults(subSuite, previousResults));
        return previousResults;
    }

}