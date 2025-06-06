package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.result.RequestResult;
import dev.pbroman.brat.core.records.runtime.RuntimeData;

public interface TestResultProcessor {

    void process(RequestResult requestResult, RuntimeData runtimeData);
}
