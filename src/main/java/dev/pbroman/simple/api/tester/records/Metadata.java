package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.api.ConfigRecord;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.result.ValidationType;

import java.util.ArrayList;
import java.util.List;

public record Metadata(String name, String description) implements ConfigRecord {

    @Override
    public List<Validation> validate() {
        var validations = new ArrayList<Validation>();
        if (name == null) {
            var message = "Metadata name is null - this will make it very difficult for you to locate failing test requests";
            validations.add(new Validation(this.getClass().getSimpleName(), null, message, ValidationType.WARN));
        }
        return validations;
    }

}
