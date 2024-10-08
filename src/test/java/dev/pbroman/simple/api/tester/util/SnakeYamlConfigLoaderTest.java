package dev.pbroman.simple.api.tester.util;

import dev.pbroman.simple.api.tester.handler.SnakeYamlConfigLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SnakeYamlConfigLoaderTest {

    @Autowired
    private SnakeYamlConfigLoader snakeYamlConfigLoader;

    @Test
    void test() throws IOException {
        //given

        //when
        var testSuite = snakeYamlConfigLoader.loadTestSuite("classpath:testconfig.yaml");

        //then
        System.out.println(testSuite);
    }

}