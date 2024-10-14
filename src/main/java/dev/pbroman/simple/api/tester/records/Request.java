package dev.pbroman.simple.api.tester.records;

import java.util.Objects;

public record Request(
        Metadata metadata,
        Condition skipCondition,
        RequestDefinition requestDefinition,
        ResponseHandling responseHandling,
        FlowControl flowControl
    ) {

    public Request {
        if (metadata == null) {
            throw new IllegalArgumentException("Request metadata cannot be null");
        }
        if (requestDefinition == null) {
            throw new IllegalArgumentException("Request definition cannot be null");
        }
    }

    public Request withRequestDefinition(RequestDefinition requestDefinition) {
        return new Request(metadata, skipCondition, requestDefinition, responseHandling, flowControl);
    }

}
