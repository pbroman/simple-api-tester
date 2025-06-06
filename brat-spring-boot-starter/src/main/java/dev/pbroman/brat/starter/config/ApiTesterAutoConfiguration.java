package dev.pbroman.brat.starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pbroman.brat.core.api.ConfigLoader;
import dev.pbroman.brat.core.api.ConfigProcessor;
import dev.pbroman.brat.core.api.HttpRequestHandler;
import dev.pbroman.brat.core.api.MessageRenderer;
import dev.pbroman.brat.core.api.RequestProcessor;
import dev.pbroman.brat.core.api.ResponseHandler;
import dev.pbroman.brat.core.api.SingleRequestRunner;
import dev.pbroman.brat.core.api.TestResultProcessor;
import dev.pbroman.brat.core.api.TestSuiteRunner;
import dev.pbroman.brat.core.control.DefaultRequestProcessor;
import dev.pbroman.brat.core.control.DefaultSingleRequestRunner;
import dev.pbroman.brat.core.control.DefaultTestResultProcessor;
import dev.pbroman.brat.core.control.DefaultTestSuiteRunner;
import dev.pbroman.brat.core.control.ValidatingConfigProcessor;
import dev.pbroman.brat.core.control.ValidationRequestProcessor;
import dev.pbroman.brat.core.handler.CheapMessageRenderer;
import dev.pbroman.brat.core.handler.DefaultHttpRequestHandler;
import dev.pbroman.brat.core.handler.DefaultResponseHandler;
import dev.pbroman.brat.core.handler.SnakeYamlConfigLoader;
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

    @Bean
    @ConditionalOnMissingBean
    public SingleRequestRunner singleRequestRunner(@Qualifier(MAIN_STACK) RequestProcessor requestProcessor) {
        return new DefaultSingleRequestRunner(requestProcessor);
    }

}
