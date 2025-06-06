package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.RequestDefinition;
import org.springframework.http.ResponseEntity;

public interface HttpRequestHandler extends RequestHandler{

    @Override
    ResponseEntity<String> performRequest(RequestDefinition requestDefinition);

}
