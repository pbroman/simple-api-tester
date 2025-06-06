package dev.pbroman.brat.core.util;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import dev.pbroman.brat.core.records.Auth;

import static dev.pbroman.brat.core.util.Constants.AUTH_TYPE_APIKEY;
import static dev.pbroman.brat.core.util.Constants.AUTH_TYPE_BASIC;
import static dev.pbroman.brat.core.util.Constants.AUTH_TYPE_BEARER;
import static dev.pbroman.brat.core.util.Constants.AUTH_TYPE_NONE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AuthHeaderCreator {

    private static final String BASIC_PREFIX = "Basic ";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String API_KEY_PREFIX = "Apikey ";

    public static Map.Entry<String, String> createAuthHeader(Auth auth) {
        var authType = auth.type().trim().toLowerCase();
        return switch (authType) {
            case AUTH_TYPE_BASIC -> createBasicAuthHeader(auth.username(), auth.password());
            case AUTH_TYPE_BEARER -> createBearerAuthHeader(auth.token());
            case AUTH_TYPE_APIKEY -> createApiKeyAuthHeader(auth.token());
            case AUTH_TYPE_NONE -> null;
            default -> throw new IllegalArgumentException("Unknown auth type: " + auth.type());
        };
    }

    private static Map.Entry<String, String> createBasicAuthHeader(String username, String password) {
        return Map.entry(AUTHORIZATION, BASIC_PREFIX + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }

    private static Map.Entry<String, String> createBearerAuthHeader(String token) {
        return Map.entry(AUTHORIZATION, BEARER_PREFIX + token);
    }

    private static Map.Entry<String, String> createApiKeyAuthHeader(String apiKey) {
        return Map.entry(AUTHORIZATION, API_KEY_PREFIX + apiKey);
    }
}
