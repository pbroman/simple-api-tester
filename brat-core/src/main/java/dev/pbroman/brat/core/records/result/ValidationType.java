package dev.pbroman.brat.core.records.result;

public enum ValidationType {

    FAIL("failed"),
    WARN("warning"),
    VALID("valid");

    private final String message;

    ValidationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
