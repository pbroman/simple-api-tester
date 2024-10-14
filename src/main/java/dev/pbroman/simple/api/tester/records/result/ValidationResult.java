package dev.pbroman.simple.api.tester.records.result;

import dev.pbroman.simple.api.tester.exception.ValidationException;
import dev.pbroman.simple.api.tester.records.Metadata;
import dev.pbroman.simple.api.tester.records.RequestDefinition;

import java.util.ArrayList;
import java.util.List;

public record ValidationResult(
        Metadata requestMetadata,
        RequestDefinition requestDefinition,
        List<ValidationException> validationExceptions
    ) {

    public ValidationResult {
        if (requestMetadata == null) {
            throw new IllegalArgumentException("Request metadata cannot be null");
        }
        if (requestDefinition == null) {
            throw new IllegalArgumentException("Request definition cannot be null");
        }
        if (validationExceptions == null) {
            validationExceptions = new ArrayList<>();
        }
    }
}
