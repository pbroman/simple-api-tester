package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.ConfigProcessor;
import dev.pbroman.simple.api.tester.records.result.ValidationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ValidatingConfigProcessorTest {

    @Autowired
    ConfigProcessor validatingConfigProcessor;

    @Test
    void testValidationWarnings() throws IOException {
        // when
        var testSuiteRuntime = validatingConfigProcessor.loadConfig("classpath:warningSuite.yaml", null);

        // then
        assertNotNull(testSuiteRuntime);
        var testSuite = testSuiteRuntime.testSuite();
        var validations = testSuite.getAllValidations();
        assertNotNull(validations);
        assertEquals(7, validations.size(), "There should be 7 validations in the test suite");
        assertEquals(7, validations.stream().filter(v -> v.validationType() == ValidationType.WARN).count(), "All validations should be warnings");
        assertTrue(validations.stream().anyMatch(v -> "POST http://example.com".equals(v.instanceName())));
        assertTrue(validations.stream().anyMatch(v -> "PUT http://example.com".equals(v.instanceName())));
        assertTrue(validations.stream().anyMatch(v -> "PATCH http://example.com".equals(v.instanceName())));
        assertTrue(validations.stream().anyMatch(v -> "subsuite with no content".equals(v.instanceName())));
        assertTrue(validations.stream().anyMatch(v -> "Anonymous".equals(v.instanceName())));

    }

    @Test
    void testValidationFails() throws IOException {
        // when
        var testSuiteRuntime = validatingConfigProcessor.loadConfig("classpath:failSuite.yaml", null);

        // then
        assertNotNull(testSuiteRuntime);
        var testSuite = testSuiteRuntime.testSuite();
        var validations = testSuite.getAllValidations();
        assertNotNull(validations);
        assertEquals(4, validations.size(), "There should be 4 validations in the test suite");
        assertEquals(4, validations.stream().filter(v -> v.validationType() == ValidationType.FAIL).count(), "All validations should be fails");
//        assertTrue(validations.stream().anyMatch(v -> "POST http://example.com".equals(v.instanceName())));
//        assertTrue(validations.stream().anyMatch(v -> "PUT http://example.com".equals(v.instanceName())));
//        assertTrue(validations.stream().anyMatch(v -> "PATCH http://example.com".equals(v.instanceName())));
//        assertTrue(validations.stream().anyMatch(v -> "subsuite with no content".equals(v.instanceName())));
//        assertTrue(validations.stream().anyMatch(v -> "Anonymous".equals(v.instanceName())));

    }



}