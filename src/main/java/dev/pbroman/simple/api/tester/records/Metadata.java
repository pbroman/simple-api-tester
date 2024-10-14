package dev.pbroman.simple.api.tester.records;

import java.util.Objects;

public record Metadata(String name, String description) {

    public Metadata {
        if (name == null) {
            throw new IllegalArgumentException("Metadata name cannot be null");
        }
    }
}
