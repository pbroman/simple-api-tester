package dev.pbroman.simple.api.tester.records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pbroman.simple.api.tester.exception.ValidationException;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.Objects;

import static dev.pbroman.simple.api.tester.util.Constants.*;

public record RequestDefinition(
        String url,
        String method,
        String timeout,
        Map<String, String> body,
        Auth auth,
        Map<String, String> headers
    ) {

    private static final Logger validationLog = LoggerFactory.getLogger(VALIDATION_LOGGER);

    public RequestDefinition {
        if (url == null) {
           throw new IllegalArgumentException("The request url cannot be null");
        }
        if (method == null) {
            validationLog.warn("No method defined for request url '{}', setting method to GET", url);
            method = "GET";
        }
        if (RequestMethod.resolve(method) == null) {
           throw new ValidationException(String.format("The method '%s' is not a valid http method", method));
        }
        if (auth == null) {
            auth = new Auth(Auth.AUTH_TYPE_NONE, null, null, null);
        }
        if ( "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) ) {
            if (body == null) {
                throw new IllegalArgumentException("Request body cannot be null for method " + method);
            }
            // check congruence between body type (defined in header) and body map
            if ( headers != null && headers.get("Content-Type") != null ) {
                if ( headers.get("Content-Type").startsWith("application/json")) {
                    if (body.get(RAW_BODY) != null) {
                        var jsonBody = body.get(RAW_BODY);
                        try {
                            new ObjectMapper().readTree(jsonBody);
                        } catch (JsonProcessingException e) {
                            throw new IllegalArgumentException("The content-type for request " + method + " " + url +
                                    " set to application/json, but the body is not valid json. Body: " + jsonBody);
                        }
                        body.put(BODY_STRING, jsonBody);
                    } else {
                        throw new IllegalArgumentException("Body type 'raw' must be present for 'application/json' content type");
                    }
                }
                else if ( headers.get("Content-Type").startsWith("application/x-www-form-urlencoded")) {
                    if (!body.isEmpty()) {
                        body.put(BODY_STRING, body.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).reduce((a, b) -> a + "&" + b).orElse(""));
                    }
                    throw new IllegalArgumentException("Form-urlencoded body must define key-value pairs for the body");
                }
            } else {
                validationLog.warn("No Content-Type set for request {} {}", method, url);
                if (body.get(RAW_BODY) != null) {
                    body.put(BODY_STRING, body.get(RAW_BODY));
                } else {
                    throw new IllegalArgumentException("No valid body definition found for request ith method " + method +
                            ". Please set the body type 'raw'");
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
