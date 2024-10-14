package dev.pbroman.simple.api.tester.util;

import dev.pbroman.simple.api.tester.exception.ValidationException;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static dev.pbroman.simple.api.tester.util.Constants.BODY;
import static dev.pbroman.simple.api.tester.util.Constants.CONSTANTS;
import static dev.pbroman.simple.api.tester.util.Constants.ENV;
import static dev.pbroman.simple.api.tester.util.Constants.HEADERS;
import static dev.pbroman.simple.api.tester.util.Constants.JSON;
import static dev.pbroman.simple.api.tester.util.Constants.RESPONSE;
import static dev.pbroman.simple.api.tester.util.Constants.STATUS_CODE;
import static dev.pbroman.simple.api.tester.util.Constants.VARS;

public class Interpolation {

    private static final Logger log = LoggerFactory.getLogger(Interpolation.class);

    private static final String variableRegex = "\\$\\{([^}]+)}";
    private static final Pattern variablePattern = Pattern.compile(variableRegex);
    private static final String arrayIndexRegex = ".+\\[(\\d+)]";
    private static final Pattern arrayIndexPattern = Pattern.compile(arrayIndexRegex);

    public static Object warnNotAllowed(String input) {
        if (input.matches(variableRegex)) {
            log.warn("Interpolation not allowed for this value, returning the raw string: {}", input);
        }
        return input;
    }

    public static Map<String, String> interpolateMap(Map<String, String> input, RuntimeData runtimeData) {
        if (input == null) {
            return null;
        }
        var interpolatedMap = new HashMap<String, String>();
        for (var entry : input.entrySet()) {
            interpolatedMap.put(entry.getKey(), (String) interpolate(entry.getValue(), runtimeData));
        }
        return interpolatedMap;
    }

    @SuppressWarnings("rawtypes")
    public static Object interpolate(Object input, RuntimeData runtimeData) {
        if (input == null) {
            return null;
        }
        if (runtimeData == null || !(input instanceof String)) {
            return input;
        }
        var inputString = String.valueOf(input);

        var matches = variablePattern.matcher(inputString);
        var valuesList = new ArrayList<>();
        Map headers = new HashMap<>();
        if (runtimeData.responseVars().get(HEADERS) instanceof Map) {
            headers = (Map) runtimeData.responseVars().get(HEADERS);
        }
        while (matches.find()) {
            var compositeVariable = matches.group(1);
            var parts = compositeVariable.split("\\.");
            switch (parts[0]) {
                case CONSTANTS:
                    guardVarSingleKey(parts);
                    valuesList.add(runtimeData.constants().get(parts[1]));
                    break;

                case VARS:
                    guardVarSingleKey(parts);
                    if (runtimeData.vars().get(parts[1]) != null) {
                        valuesList.add(runtimeData.vars().get(parts[1]).toString());
                    } else {
                        throw new ValidationException("The variable " + parts[1] + " has not been set");
                    }
                    break;

                case ENV:
                    guardVarSingleKey(parts);
                    valuesList.add(runtimeData.env().get(parts[1]));
                    break;
                    
                case RESPONSE:
                    if (parts.length == 1) {
                        throw new IllegalArgumentException("The 'response' variable must have at least one key, e.g. ${response.statusCode} or ${response.json}, got : " + String.join(".", parts));
                    } else {
                        var subparts = Arrays.copyOfRange(parts, 1, parts.length);
                        switch (StringUtils.substringBefore(subparts[0], "[")) {
                            case STATUS_CODE:
                                guardSoleSingleKey(subparts);
                                valuesList.add(runtimeData.responseVars().get(STATUS_CODE).toString());
                                break;

                            case HEADERS:
                                guardVarSingleKey(subparts);
                                if (headers.containsKey(subparts[1])) {
                                    valuesList.add(headers.get(subparts[1]).toString());
                                } else {
                                    valuesList.add("");
                                }
                                break;

                            case BODY:
                                guardSoleSingleKey(subparts);
                                if (runtimeData.responseVars().get(BODY) != null) {
                                    valuesList.add(runtimeData.responseVars().get(BODY).toString());
                                }
                                break;

                            case JSON:
                                valuesList.add(getJsonValue(runtimeData.responseVars().get(JSON), subparts));
                                break;

                            default:
                                throw new IllegalArgumentException("Unknown response variable type: " + subparts[0]);
                        }
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unknown variable type: " + parts[0]);
            }
        }

        if (inputString.matches(variableRegex)) {
            return valuesList.getFirst();
        }
        matches = variablePattern.matcher(inputString);
        while (matches.find()) {
            inputString = inputString.replaceFirst(Pattern.quote(matches.group()), valuesList.removeFirst().toString());
        }
        return inputString;
    }

    private static Object getJsonValue(Object body, String[] parts) {
        // parts is here e.g. '["json", "array[0]", "test"]'
        if (body == null) {
            return null;
        }
        if (!(body instanceof JSONObject) && !(body instanceof JSONArray)) {
            throw new IllegalArgumentException("Invalid json in response body");
        }
        var json = new JSONObject().put("json", body);
        for (int i = 0; i < parts.length; i++) {
            var varPart = StringUtils.substringBefore(parts[i], "[");
            if (json.has(varPart)) {
                try {
                    var indexMatch = arrayIndexPattern.matcher(parts[i]);
                    if (indexMatch.find()) {
                        var jsonArray = json.getJSONArray(varPart);
                        json = (JSONObject) jsonArray.get(Integer.parseInt(indexMatch.group(1)));
                    } else if (partIsFunction(parts, i)) {
                        return interpolateFunction(json.get(varPart), parts[i+1]);
                    } else if (json.get(varPart) instanceof JSONArray) {
                        return json.getJSONArray(varPart);
                    } else if (json.get(varPart) instanceof JSONObject) {
                        json = json.getJSONObject(varPart);
                    } else if (json.get(varPart) instanceof String) {
                        return json.getString(varPart);
                    } else {
                        throw new IllegalArgumentException("Unknown type of json value: " + json.get(varPart) + " in body " + body);
                    }
                } catch (JSONException e) {
                    throw new ValidationException("Could not find the json path in the response body. Path: "
                            + StringUtils.joinWith(".", parts) + ", body: " + body, e);
                }
            }
            else if (parts[i].startsWith("_")) {
                return interpolateFunction(json, parts[i]);
            }
        }
        return json;
    }

    private static boolean partIsFunction(String[] parts, int i) {
        return i < parts.length-1 && parts[i+1].startsWith("_");
    }

    private static Object interpolateFunction(Object json, String varPart) {
        return switch (varPart) {
            case "_length" -> {
                switch (json) {
                    case JSONArray array -> {
                        yield array.length();
                    }
                    case JSONObject jsonObject -> {
                        yield jsonObject.length();
                    }
                    case String s -> {
                        yield s.length();
                    }
                    case null, default -> throw new IllegalArgumentException("Can't get length of unknown object");
                }
            }
            case "_isArray" -> json instanceof JSONArray;
            case "_isObject" -> json instanceof JSONObject;
            case "_isString" -> json instanceof String;
            default -> throw new IllegalArgumentException("Unknown function: " + varPart);
        };
    }

    private static void guardVarSingleKey(String[] parts) {
        if (parts.length == 1 || parts.length > 2) {
            throw new IllegalArgumentException("This kind of variable must have exactly one key, e.g. ${constants.foo} or ${env.baseUrl}, got : " + String.join(".", parts));
        }
    }

    private static void guardSoleSingleKey(String[] parts) {
        if (parts.length > 1) {
            throw new IllegalArgumentException("This kind of response variable mustn't have children at all, e.g. ${response.statusCode} or ${response.body}, got : " + String.join(".", parts));
        }
    }


}
