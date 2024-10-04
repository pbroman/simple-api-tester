package dev.pbroman.simple.api.tester.records;

import java.util.Objects;

public record Metadata(String name, String description) {

    public Metadata {
        Objects.requireNonNull(name, "Metadata name cannot be null");
    }
}
