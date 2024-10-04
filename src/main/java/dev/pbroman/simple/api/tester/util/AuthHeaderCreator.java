package dev.pbroman.simple.api.tester.util;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import dev.pbroman.simple.api.tester.records.Auth;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AuthHeaderCreator {

    private static final String BASIC = "Basic ";
    private static final String BEARER = "Bearer ";
    private static final String API_KEY = "Apikey ";

    public static Map.Entry<String, String> createAuthHeader(Auth auth) {
        return switch (auth.type()) {
            case BASIC -> createBasicAuthHeader(auth.username(), auth.password());
            case BEARER -> createBearerAuthHeader(auth.token());
            case API_KEY -> createApiKeyAuthHeader(auth.token());
            default -> throw new IllegalArgumentException("Unknown auth type: " + auth.type());
        };
    }

    private static Map.Entry<String, String> createBasicAuthHeader(String username, String password) {
        return Map.entry(AUTHORIZATION, BASIC + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }

    private static Map.Entry<String, String> createBearerAuthHeader(String token) {
        return Map.entry(AUTHORIZATION, BEARER + token);
    }

    private static Map.Entry<String, String> createApiKeyAuthHeader(String apiKey) {
        return Map.entry(AUTHORIZATION, API_KEY + apiKey);
    }
}
