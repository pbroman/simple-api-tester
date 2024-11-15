package dev.pbroman.simple.api.tester.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pbroman.simple.api.tester.api.ConfigLoader;
import dev.pbroman.simple.api.tester.api.ConfigProcessor;
import dev.pbroman.simple.api.tester.api.HttpRequestHandler;
import dev.pbroman.simple.api.tester.api.MessageRenderer;
import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.api.ResponseHandler;
import dev.pbroman.simple.api.tester.api.TestResultProcessor;
import dev.pbroman.simple.api.tester.api.TestSuiteRunner;
import dev.pbroman.simple.api.tester.control.DefaultRequestProcessor;
import dev.pbroman.simple.api.tester.control.DefaultTestResultProcessor;
import dev.pbroman.simple.api.tester.control.DefaultTestSuiteRunner;
import dev.pbroman.simple.api.tester.control.ValidatingConfigProcessor;
import dev.pbroman.simple.api.tester.control.ValidationRequestProcessor;
import dev.pbroman.simple.api.tester.handler.CheapMessageRenderer;
import dev.pbroman.simple.api.tester.handler.DefaultHttpRequestHandler;
import dev.pbroman.simple.api.tester.handler.DefaultResponseHandler;
import dev.pbroman.simple.api.tester.handler.SnakeYamlConfigLoader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;

@AutoConfiguration
@AutoConfigureAfter( name = "org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class")
public class ApiTesterAutoConfiguration {

    public static final String MAIN_STACK = "main";
    public static final String VALIDATION_STACK = "validation";

    @Bean
    @Qualifier(MAIN_STACK)
    @ConditionalOnMissingBean
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

    @Bean
    @ConditionalOnMissingBean
    public MessageRenderer messageRenderer() {
        return new CheapMessageRenderer();
    }

    @Bean
    @ConditionalOnMissingBean
    public TestResultProcessor testResultProcessor(MessageRenderer messageRenderer) {
        return new DefaultTestResultProcessor(messageRenderer);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigLoader configLoader(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        return new SnakeYamlConfigLoader(objectMapper, resourceLoader);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigProcessor configProcessor(ConfigLoader configLoader, @Qualifier(VALIDATION_STACK) TestSuiteRunner testSuiteRunner) {
        return new ValidatingConfigProcessor(configLoader, testSuiteRunner);
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseHandler responseHandler() {
        return new DefaultResponseHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpRequestHandler httpRequestHandler() {
        return new DefaultHttpRequestHandler();
    }

}
