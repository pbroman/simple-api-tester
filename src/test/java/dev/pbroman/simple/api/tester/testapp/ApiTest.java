package dev.pbroman.simple.api.tester.testapp;

import dev.pbroman.simple.api.tester.handler.DefaultHttpRequestHandler;
import dev.pbroman.simple.api.tester.records.RequestDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration( classes = { CrudApplication.class } )
public class ApiTest {

    @LocalServerPort
    private int port;

    private String baseUrl() {
        return "http://localhost:" + port;
    }


    @Test
    void test() {
        // given
        var url = baseUrl() + "/id";

        // when
        var response = new DefaultHttpRequestHandler().performRequest(new RequestDefinition(baseUrl() + "/id", "GET", null, null, null, null));

        // then
        assertNotNull(response);
    }
}
