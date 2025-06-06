package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.result.Validation;
import dev.pbroman.brat.core.records.runtime.RuntimeData;

import java.util.List;

public interface ConfigRecord {

    default ConfigRecord interpolated(RuntimeData runtimeData) {
        return this;
    }

    List<Validation> validate();

}
