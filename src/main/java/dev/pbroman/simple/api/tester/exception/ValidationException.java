package dev.pbroman.simple.api.tester.exception;

import dev.pbroman.simple.api.tester.records.result.ValidationType;

public class ValidationException extends RuntimeException {

    private ValidationType validationType;

    public ValidationException(String message, ValidationType type) {
        super(message);
        this.validationType = type;
    }

    public ValidationException(String message, ValidationType type, Throwable cause) {
        super(message, cause);
        this.validationType = type;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

}
