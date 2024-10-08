package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

import java.util.Map;
import java.util.Objects;

import static dev.pbroman.simple.api.tester.util.Constants.BODY_STRING;
import static dev.pbroman.simple.api.tester.util.Constants.RAW_BODY;

public record RequestDefinition(
        String url,
        String method,
        String timeout,
        Map<String, String> body,
        Auth auth,
        Map<String, String> headers
    ) {

    public RequestDefinition {
        Objects.requireNonNull(url, "Request url cannot be null");
        Objects.requireNonNull(method, "Request method cannot be null");
        if (auth == null) {
            auth = new Auth(Auth.AUTH_TYPE_NONE, null, null, null);
        }
        if ( "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) ) {
            Objects.requireNonNull(body, "Request body cannot be null for method " + method);
            // check congruence between body type (defined in header) and body map
            if ( headers != null && headers.get("Content-Type") != null ) {
                if ( headers.get("Content-Type").startsWith("application/json")) {
                    if (body != null && body.get(RAW_BODY) != null) {
                        body.put(BODY_STRING, body.get(RAW_BODY));
                    } else {
                        throw new IllegalArgumentException("Body type 'raw' must be present for 'application/json' content type");
                    }
                }
                else if ( headers.get("Content-Type").startsWith("application/x-www-form-urlencoded")) {
                    if (body != null && !body.isEmpty()) {
                        body.put(BODY_STRING, body.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).reduce((a, b) -> a + "&" + b).orElse(""));
                    }
                    throw new IllegalArgumentException("Form url encoded body must define key-value pairs for the body");
                }
            } else {
                if (body.get(RAW_BODY) != null) {
                    body.put(BODY_STRING, body.get(RAW_BODY));
                } else {
                    throw new IllegalArgumentException("No valid body definition found. Please set the body type 'raw'");
                }
            }
        }
    }

    public RequestDefinition withAuth(Auth auth) {
        return new RequestDefinition(url, method, timeout, body, auth, headers);
    }

    public RequestDefinition withTimeout(String timeout) {
        return new RequestDefinition(url, method, timeout, body, auth, headers);
    }

    public RequestDefinition interpolated(RuntimeData runtimeData) {
        var interpolatedUrl = Interpolation.interpolate(url, runtimeData).toString();
        var interpolatedMethod = Interpolation.interpolate(method, runtimeData).toString();
        var interpolatedTimeout = Interpolation.interpolate(timeout, runtimeData).toString();
        var interpolatedBody = Interpolation.interpolateMap(body, runtimeData);
        var interpolatedAuth = auth != null ? auth.interpolated(runtimeData) : null;
        var interpolatedHeaders = Interpolation.interpolateMap(headers, runtimeData);
        return new RequestDefinition(interpolatedUrl, interpolatedMethod, interpolatedTimeout, interpolatedBody, interpolatedAuth, interpolatedHeaders);
    }
}
