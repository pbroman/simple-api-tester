package dev.pbroman.simple.api.tester.config;

import dev.pbroman.simple.api.tester.api.HttpRequestHandler;
import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.api.ResponseHandler;
import dev.pbroman.simple.api.tester.api.TestResultProcessor;
import dev.pbroman.simple.api.tester.api.TestSuiteRunner;
import dev.pbroman.simple.api.tester.control.DefaultRequestProcessor;
import dev.pbroman.simple.api.tester.control.DefaultTestSuiteRunner;
import dev.pbroman.simple.api.tester.control.ValidationRequestProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
public class ApiTesterConfig {

    public static final String MAIN_STACK = "main";
    public static final String VALIDATION_STACK = "validation";

    @Bean
    @Qualifier(MAIN_STACK)
    public RequestProcessor defaultRequestProcessor(HttpRequestHandler httpRequestHandler,
                                                    ResponseHandler responseHandler,
                                                    TestResultProcessor testResultProcessor) {
        return new DefaultRequestProcessor(httpRequestHandler, responseHandler, testResultProcessor);
    }

    @Bean
    @Primary
    @Qualifier(MAIN_STACK)
    public TestSuiteRunner testSuiteRunner(@Qualifier(MAIN_STACK) RequestProcessor requestProcessor) {
        return new DefaultTestSuiteRunner(requestProcessor);
    }

    @Bean
    @Qualifier(VALIDATION_STACK)
    public RequestProcessor validationRequestProcessor() {
        return new ValidationRequestProcessor();
    }

    @Bean
    @Qualifier(VALIDATION_STACK)
    public TestSuiteRunner validationTestSuiteRunner(@Qualifier(VALIDATION_STACK) RequestProcessor requestProcessor) {
        return new DefaultTestSuiteRunner(requestProcessor);
    }

}
