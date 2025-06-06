package dev.pbroman.brat.starter.util;

import dev.pbroman.brat.core.api.ConfigLoader;
import dev.pbroman.brat.starter.config.ApiTesterAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

@SpringBootTest
@ContextConfiguration(classes = {ApiTesterAutoConfiguration.class, JacksonAutoConfiguration.class})
class SnakeYamlConfigLoaderTest {

    @Autowired
    private ConfigLoader snakeYamlConfigLoader;

    @Test
    void test() throws IOException {
        //given

        //when
        var testSuite = snakeYamlConfigLoader.loadTestSuite("classpath:testconfig.yaml");

        //then
        System.out.println(testSuite);
    }

}