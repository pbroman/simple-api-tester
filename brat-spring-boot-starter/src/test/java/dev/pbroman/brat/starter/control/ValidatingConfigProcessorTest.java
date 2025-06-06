package dev.pbroman.brat.starter.control;

import dev.pbroman.brat.core.api.ConfigProcessor;
import dev.pbroman.brat.starter.config.ApiTesterAutoConfiguration;
import dev.pbroman.brat.core.records.result.ValidationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {ApiTesterAutoConfiguration.class, JacksonAutoConfiguration.class})
class ValidatingConfigProcessorTest {

    @Autowired
    ConfigProcessor validatingConfigProcessor;

    @Test
    void testValidationWarnings() throws IOException {
        // given
        var numExpectedWarnings = 6;

        // when
        var testSuiteRuntime = validatingConfigProcessor.loadConfig("classpath:warningSuite.yaml", null);

        // then
        assertNotNull(testSuiteRuntime);
        var validations = testSuiteRuntime.runtimeData().validations();
        assertNotNull(validations);
        assertEquals(numExpectedWarnings, validations.size(), "There should be " + numExpectedWarnings + " validations in the test suite");
        assertEquals(numExpectedWarnings, validations.stream().filter(v -> v.validationType() == ValidationType.WARN).count(), "All validations should be warnings");
        assertTrue(validations.stream().anyMatch(v -> "POST http://example.com".equals(v.instanceName())));
        assertTrue(validations.stream().anyMatch(v -> "PUT http://example.com".equals(v.instanceName())));
        assertTrue(validations.stream().anyMatch(v -> "PATCH http://example.com".equals(v.instanceName())));
        assertTrue(validations.stream().anyMatch(v -> "subsuite with no content".equals(v.instanceName())));
        assertTrue(validations.stream().anyMatch(v -> "Anonymous".equals(v.instanceName())));

    }

    @Test
    void testValidationFails() throws IOException {
        // given
        var numExpectedFails = 17;

        // when
        var testSuiteRuntime = validatingConfigProcessor.loadConfig("classpath:failSuite.yaml", null);

        // then
        assertNotNull(testSuiteRuntime);
        var validations = testSuiteRuntime.runtimeData().validations();
        assertNotNull(validations);
        assertEquals(numExpectedFails, validations.size(), "There should be " + numExpectedFails + " validations in the test suite");
        assertEquals(numExpectedFails, validations.stream().filter(v -> v.validationType() == ValidationType.FAIL).count(), "All validations should be fails");
//        assertTrue(validations.stream().anyMatch(v -> "POST http://example.com".equals(v.instanceName())));
//        assertTrue(validations.stream().anyMatch(v -> "PUT http://example.com".equals(v.instanceName())));
//        assertTrue(validations.stream().anyMatch(v -> "PATCH http://example.com".equals(v.instanceName())));
//        assertTrue(validations.stream().anyMatch(v -> "subsuite with no content".equals(v.instanceName())));
//        assertTrue(validations.stream().anyMatch(v -> "Anonymous".equals(v.instanceName())));

    }



}