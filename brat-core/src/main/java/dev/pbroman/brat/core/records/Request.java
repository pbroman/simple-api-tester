package dev.pbroman.brat.core.records;

import dev.pbroman.brat.core.api.ConfigRecord;
import dev.pbroman.brat.core.records.result.Validation;
import dev.pbroman.brat.core.records.result.ValidationType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Request(
        Metadata metadata,
        Condition skipCondition,
        RequestDefinition requestDefinition,
        ResponseHandling responseHandling,
        FlowControl flowControl,
        String id
    ) implements ConfigRecord {

    public Request {
        if (id == null) {
            // This is for a future GUI to be able to identify the request
            id = UUID.randomUUID().toString();
        }
    }

    @Override
    public List<Validation> validate() {
        var validations = new ArrayList<Validation>();
        if (metadata == null) {
            validations.add(validation("Request metadata should not be null", ValidationType.WARN));
        } else {
            validations.addAll(metadata.validate());
        }

        if (requestDefinition == null) {
            validations.add(validation("Request definition cannot be null", ValidationType.FAIL));
        } else {
            validations.addAll(requestDefinition.validate());
        }

        if (responseHandling != null) {
            validations.addAll(responseHandling.validate());
        }
        if (flowControl != null) {
            validations.addAll(flowControl.validate());
        }
        if (skipCondition != null) {
            validations.addAll(skipCondition.validate());
        }

        return validations;
    }

    private Validation validation(String message, ValidationType validationType) {
        var instanceName = metadata != null ? metadata.name() : null;
        return new Validation(this.getClass().getSimpleName(), instanceName, message, validationType);
    }

    public Request withRequestDefinition(RequestDefinition requestDefinition) {
        return new Request(metadata, skipCondition, requestDefinition, responseHandling, flowControl, id);
    }

}
