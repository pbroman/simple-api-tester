package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.RequestDefinition;

public interface RequestHandler {

    Object performRequest(RequestDefinition requestDefinition);

}
