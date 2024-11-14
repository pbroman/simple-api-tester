package dev.pbroman.simple.api.tester.records.runtime;

import dev.pbroman.simple.api.tester.records.result.RequestResult;
import dev.pbroman.simple.api.tester.records.result.Validation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.pbroman.simple.api.tester.util.Constants.BODY;
import static dev.pbroman.simple.api.tester.util.Constants.HEADERS;
import static dev.pbroman.simple.api.tester.util.Constants.JSON;
import static dev.pbroman.simple.api.tester.util.Constants.STATUS_CODE;

public record RuntimeData(
        Map<String, String> constants,
        Map<String, String> env,
        Map<String, Object> vars,
        Map<String, Object> responseVars,
        List<RequestResult> requestResults,
        List<Validation> validations,
        String currentPath,
        int currentRequestNo
    ) {

    public RuntimeData {
        if (env == null) {
            env = new HashMap<>();
        }
        if (vars == null) {
            vars = new HashMap<>();
        }
        if (responseVars == null) {
            responseVars = new HashMap<>();
        }
        if (requestResults == null) {
            requestResults = new ArrayList<>();
        }
        if (validations == null) {
            validations = new ArrayList<>();
        }
        if (currentPath == null) {
            currentPath = "";
        }
        if (currentRequestNo < 0) {
            currentRequestNo = 0;
        }
    }

    public RuntimeData(Map<String, String> constants, Map<String, String> env) {
        this(constants, env, null, null, null, null, "", 0);
    }

    public RuntimeData(Map<String, String> constants, Map<String, String> env, Map<String, Object> vars, Map<String, Object> responseVars) {
        this(constants, env, vars, responseVars, null, null, "", 0);
    }

    public RuntimeData withHttpResponseVars(ResponseEntity<String> response) {
        var newResponseVars = new HashMap<String, Object>();
        var headers = new HashMap<>();
        response.getHeaders().forEach((key, value) -> headers.put(key, value.getFirst()));
        newResponseVars.put(HEADERS, headers);
        newResponseVars.put(STATUS_CODE, response.getStatusCode().value());
        newResponseVars.put(BODY, response.getBody());
        if (response.getBody() != null) {
            try {
                newResponseVars.put(JSON, new JSONObject(response.getBody()));
            } catch (JSONException e) {
                try {
                    newResponseVars.put(JSON, new JSONArray(response.getBody()));
                } catch (JSONException e1) {
                    // noop
                }
            }
        }
        return new RuntimeData(constants, env, vars, newResponseVars, requestResults, validations, currentPath, currentRequestNo);
    }

    public RuntimeData withCurrentPath(String path) {
        return new RuntimeData(constants, env, vars, responseVars, requestResults, validations, path, currentRequestNo);
    }

    public RuntimeData incrementRequestNo() {
        return new RuntimeData(constants, env, vars, responseVars, requestResults, validations, currentPath, currentRequestNo + 1);
    }
}
