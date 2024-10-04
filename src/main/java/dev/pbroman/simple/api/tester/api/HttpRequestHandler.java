package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.RequestDefinition;
import org.springframework.http.ResponseEntity;

public interface HttpRequestHandler extends RequestHandler{

    @Override
    ResponseEntity<String> performRequest(RequestDefinition requestDefinition);

}
