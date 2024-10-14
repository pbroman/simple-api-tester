package dev.pbroman.simple.api.tester.records.runtime;

import dev.pbroman.simple.api.tester.records.TestSuite;
import dev.pbroman.simple.api.tester.records.result.TestResult;
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
        TestSuite currentTestSuite,
        Map<TestSuite, List<Object>> results
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
        if (results == null) {
            results = new HashMap<>();
        }
    }

    public RuntimeData(Map<String, String> constants, Map<String, String> env) {
        this(constants, env, null, null, null, null);
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
        return new RuntimeData(constants, env, vars, newResponseVars, currentTestSuite, results);
    }

    public RuntimeData withCurrentTestSuite(TestSuite testSuite) {
        return new RuntimeData(constants, env, vars, responseVars, testSuite, results);
    }

    public void addTestResult(TestResult testResult) {
        results.computeIfAbsent(currentTestSuite, k -> new ArrayList<>());
        results.get(currentTestSuite).add(testResult);
    }
}
