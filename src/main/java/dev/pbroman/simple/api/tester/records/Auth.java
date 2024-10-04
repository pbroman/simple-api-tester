package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

import java.util.List;

public record Auth(String type, String username, String password, String token) {

    public static final String AUTH_TYPE_BASIC = "basic";
    public static final String AUTH_TYPE_BEARER = "bearer";
    public static final String AUTH_TYPE_APIKEY = "apikey";
    public static final String AUTH_TYPE_NONE = "none";
    public static final List<String> AUTH_TYPES = List.of(AUTH_TYPE_NONE, AUTH_TYPE_BASIC, AUTH_TYPE_BEARER, AUTH_TYPE_APIKEY);

    public Auth {
        if (type == null) {
            throw new IllegalArgumentException("Auth type cannot be null");
        }
        if (!AUTH_TYPES.contains(type)) {
            throw new IllegalArgumentException("Auth type must be one of 'none', 'basic', 'bearer', or 'apikey'");
        }
        switch (type) {
            case AUTH_TYPE_BASIC -> {
                if (username == null) {
                    throw new IllegalArgumentException("Username cannot be null for basic auth");
                }
                if (password == null) {
                    throw new IllegalArgumentException("Password cannot be null for basic auth");
                }
            }
            case AUTH_TYPE_BEARER -> {
                if (token == null) {
                    throw new IllegalArgumentException("Token cannot be null for bearer auth");
                }
            }
            case AUTH_TYPE_APIKEY -> {
                if (token == null) {
                    throw new IllegalArgumentException("Token cannot be null for apikey auth");
                }
            }
        }
    }

    public Auth interpolated(RuntimeData runtimeData) {
        var interpolatedUsername = Interpolation.interpolate(username, runtimeData).toString();
        var interpolatedPassword = Interpolation.interpolate(password, runtimeData).toString();
        var interpolatedToken = Interpolation.interpolate(token, runtimeData).toString();
        return new Auth(type, interpolatedUsername, interpolatedPassword, interpolatedToken);
    }
}
