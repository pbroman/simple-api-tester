package dev.pbroman.simple.api.tester.api;

import dev.pbroman.simple.api.tester.records.Request;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;

/**
 * A RequestProcessor processes a {@link Request}, including response handling and flow control.
 * The {@link RuntimeData} is updated according to the result.
 */
public interface RequestProcessor {

    void processRequest(Request request, RuntimeData runtimeData);
}
