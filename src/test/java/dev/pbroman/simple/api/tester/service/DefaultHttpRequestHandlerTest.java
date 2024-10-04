package dev.pbroman.simple.api.tester.service;

import dev.pbroman.simple.api.tester.handler.DefaultHttpRequestHandler;
import dev.pbroman.simple.api.tester.records.Auth;
import dev.pbroman.simple.api.tester.util.AuthHeaderCreator;
import dev.pbroman.simple.api.tester.records.RequestDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultHttpRequestHandlerTest {

    private final DefaultHttpRequestHandler defaultHttpRequestHandler = new DefaultHttpRequestHandler();

    @Test
    void performRequest() {
        // given
        var url = "https://scim-proxy.dev.cornelsen.de/health";

        // when
        var response = defaultHttpRequestHandler.performRequest(new RequestDefinition(url, "GET", null, null, null, Map.of("moo", "baa", "Content-Type", "application/json")));

        // then
        assertNotNull(response);
        response.getStatusCode().value();

    }

    @Test
    void auth() {
        // given
        var url = "https://scim-proxy.dev.cornelsen.de/health";
        var basicAuth = AuthHeaderCreator.createAuthHeader(new Auth(Auth.AUTH_TYPE_BASIC, "user", "password", null));
        var headers = Map.ofEntries(basicAuth);


    }
}