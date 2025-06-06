package dev.pbroman.brat.core.api;

import dev.pbroman.brat.core.records.Request;
import dev.pbroman.brat.core.records.runtime.RuntimeData;

/**
 * A RequestProcessor processes a {@link Request}, including response handling and flow control.
 * The {@link RuntimeData} is updated according to the result.
 */
public interface RequestProcessor {

    void processRequest(Request request, RuntimeData runtimeData);
}
