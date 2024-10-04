package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.RequestDefinition;

public interface RequestHandler {

    Object performRequest(RequestDefinition requestDefinition);

}
