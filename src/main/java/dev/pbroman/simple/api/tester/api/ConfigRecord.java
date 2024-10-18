package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;

import java.util.List;

public interface ConfigRecord {

    default ConfigRecord interpolated(RuntimeData runtimeData) {
        return this;
    }

    List<Validation> validate();

}
