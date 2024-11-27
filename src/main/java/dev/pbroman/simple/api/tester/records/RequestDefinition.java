package dev.pbroman.simple.api.tester.records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pbroman.simple.api.tester.api.ConfigRecord;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.result.ValidationType;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.pbroman.simple.api.tester.util.Constants.*;

public record RequestDefinition(
        String url,
        String method,
        String timeout,
        Map<String, String> body,
        Auth auth,
        Map<String, String> headers
    ) implements ConfigRecord {

    private static final Logger validationLog = LoggerFactory.getLogger(VALIDATION_LOGGER);

    @Override
    public List<Validation> validate() {
        var validations = new ArrayList<Validation>();
        if (url == null) {
            validations.add(validation("Request url cannot be null", ValidationType.FAIL));
        }
        if (method == null) {
            validations.add(validation("Request method cannot be null", ValidationType.FAIL));
        } else if (RequestMethod.resolve(method) == null) {
            validations.add(validation("The method '" + method + "' is not a valid http method", ValidationType.FAIL));
        }

        if ( "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) ) {
            if (body == null) {
                validations.add(validation("Request body cannot be null for method " + method, ValidationType.FAIL));
            } else if ( headers != null && headers.get("Content-Type") != null ) {
                // check congruence between body type (defined in header) and body map
                if ( headers.get("Content-Type").startsWith("application/json")) {
                    if (body.get(RAW_BODY) != null) {
                        var jsonBody = body.get(RAW_BODY);
                        try {
                            new ObjectMapper().readTree(jsonBody);
                            body.put(BODY_STRING, jsonBody);
                        } catch (JsonProcessingException e) {
                            var message = "The content-type for request " + method + " " + url +
                                    " set to application/json, but the body is not valid json. Body: " + jsonBody;
                            validations.add(validation(message, ValidationType.FAIL));
                        }
                    } else {
                        validations.add(validation("Body type 'raw' must be present for 'application/json' content type", ValidationType.FAIL));
                    }
                }
                else if ( headers.get("Content-Type").startsWith("application/x-www-form-urlencoded")) {
                    if (!body.isEmpty()) {
                        body.put(BODY_STRING, body.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).reduce((a, b) -> a + "&" + b).orElse(""));
                    } else {
                        validations.add(validation("Form-urlencoded body must define key-value pairs for the body", ValidationType.FAIL));
                    }
                }
            } else {
                validations.add(validation("No Content-Type set for request " + method + " " + url, ValidationType.WARN));
                // TODO it might be dangerous having this here - it presupposes that validation is always called
                if (body.get(RAW_BODY) != null) {
                    body.put(BODY_STRING, body.get(RAW_BODY));
                } else {
                    var message = "No valid body definition found for request with method " + method +
                            ". Please set the body type 'raw'";
                    validations.add(validation(message, ValidationType.FAIL));
                }
            }
        }
        if (auth != null) {
            validations.addAll(auth.validate());
        }

        return validations;
    }

    private Validation validation(String message, ValidationType validationType) {
        return new Validation(this.getClass().getSimpleName(), method + " " + url, message, validationType);
    }

    @Override
    public RequestDefinition interpolated(RuntimeData runtimeData) {
        var interpolatedUrl = String.valueOf(Interpolation.interpolate(url, runtimeData));
        var interpolatedMethod = String.valueOf(Interpolation.interpolate(method, runtimeData));
        var interpolatedTimeout = String.valueOf(Interpolation.interpolate(timeout, runtimeData));
        var interpolatedBody = Interpolation.interpolateMap(body, runtimeData);
        var interpolatedAuth = auth != null ? auth.interpolated(runtimeData) : null;
        var interpolatedHeaders = Interpolation.interpolateMap(headers, runtimeData);
        return new RequestDefinition(interpolatedUrl, interpolatedMethod, interpolatedTimeout, interpolatedBody, interpolatedAuth, interpolatedHeaders);
    }

    public RequestDefinition withAuth(Auth auth) {
        return new RequestDefinition(url, method, timeout, body, auth, headers);
    }

    public RequestDefinition withTimeout(String timeout) {
        return new RequestDefinition(url, method, timeout, body, auth, headers);
    }

}
