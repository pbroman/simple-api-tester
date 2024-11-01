package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.testapp.CrudApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootTest
class TestSuiteRunnerTest {

    @Autowired
    private ValidatingConfigProcessor configProcessor;

    @Autowired
    private DefaultTestSuiteRunner testSuiteRunner;

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
        var testSuiteRuntime = configProcessor.loadConfig("classpath:crudApiSpec.yaml", null);
        testSuiteRuntime.runtimeData().env().put("baseUrl", "http://localhost:8080");

        // when
        testSuiteRunner.run(testSuiteRuntime);

        // then
//        assertNotNull(testResults);
//        assertEquals(testSuiteRuntime.testSuite().subSuites().size(), testResults.size());
//          // check there are no failing assertions
//        testResults.forEach(
//                (suite, results) -> results.forEach(
//                        result -> ((RequestResult) result).assertionResults().forEach(
//                                assertion -> assertTrue(assertion.passed()))) );
    }

}