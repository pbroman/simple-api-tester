package dev.pbroman.simple.api.tester.util;

import dev.pbroman.simple.api.tester.records.Auth;

public class Constants {

    public static final String PROTOCOL_LOGGER = "protocol";

    public static final String CONSTANTS = "constants";
    public static final String HEADERS = "headers";
    public static final String ENV = "env";
    public static final String VARS = "vars";
    public static final String RESPONSE = "response";
    public static final String STATUS_CODE = "statusCode";
    public static final String BODY = "body";
    public static final String RAW_BODY = "raw";
    public static final String BODY_STRING = "_bodyString";
    public static final String JSON = "json";

    public static final String DEFAULT_TIMEOUT_MS = "30000";
    public static final Auth DEFAULT_AUTH = new Auth("none", null, null, null);

    public static final Integer DEFAULT_MAX_ATTEMPTS = 3;




}
