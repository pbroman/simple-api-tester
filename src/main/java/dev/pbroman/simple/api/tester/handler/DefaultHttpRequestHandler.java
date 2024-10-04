package dev.pbroman.simple.api.tester.handler;

import dev.pbroman.simple.api.tester.api.HttpRequestHandler;
import dev.pbroman.simple.api.tester.records.RequestDefinition;
import dev.pbroman.simple.api.tester.util.AuthHeaderCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static dev.pbroman.simple.api.tester.util.Constants.BODY_STRING;
import static dev.pbroman.simple.api.tester.util.Constants.PROTOCOL_LOGGER;

@Service
public class DefaultHttpRequestHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultHttpRequestHandler.class);
    private static final Logger protocol = LoggerFactory.getLogger(PROTOCOL_LOGGER);

    private static final List<RequestMethod> METHODS_WITH_BODY = List.of(RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH);

    @Override
    public ResponseEntity<String> performRequest(RequestDefinition requestDefinition) {

        RequestMethod method = RequestMethod.resolve(requestDefinition.method().toUpperCase());
        if (method == null) {
            throw new IllegalArgumentException("Invalid request method: " + requestDefinition.method());
        }

        var clientRequestFactory = new HttpComponentsClientHttpRequestFactory();
        try {
            var timeout = Integer.parseInt(requestDefinition.timeout());
            clientRequestFactory.setConnectTimeout(timeout);
            clientRequestFactory.setConnectionRequestTimeout(timeout);
        } catch (NumberFormatException e) {
            log.warn("Invalid timeout value: {}, not setting a timeout", requestDefinition.timeout());
        }

        var requestBodySpec = RestClient.builder()
                .requestFactory(clientRequestFactory)
                .build()
                .method(method.asHttpMethod())
                .uri(requestDefinition.url());

        if (METHODS_WITH_BODY.contains(method) && requestDefinition.body() != null)  {
            requestBodySpec.body(requestDefinition.body().get(BODY_STRING));
        }

        if (requestDefinition.headers() != null) {
            requestDefinition.headers().forEach(requestBodySpec::header);
        }

        if (requestDefinition.auth() != null) {
            Map.Entry<String, String> authHeader = AuthHeaderCreator.createAuthHeader(requestDefinition.auth());
            requestBodySpec.header(authHeader.getKey(), authHeader.getValue());
        }

        return requestBodySpec.retrieve().toEntity(String.class);
    }

}
