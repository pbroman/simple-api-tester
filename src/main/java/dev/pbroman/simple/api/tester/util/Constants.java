package dev.pbroman.simple.api.tester.util;

import dev.pbroman.simple.api.tester.records.Auth;

public class Constants {

    public static final String PROTOCOL_LOGGER = "simple-api-tester-protocol";
    public static final String VALIDATION_LOGGER = "simple-api-tester-validation";

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
    public static final Auth AUTH_NONE = new Auth("none", null, null, null);

    public static final Integer DEFAULT_MAX_ATTEMPTS = 3;

    public static final String PATH_DELIMITER = " |o| ";

    public static final String AUTH_TYPE_BASIC = "basic";
    public static final String AUTH_TYPE_BEARER = "bearer";
    public static final String AUTH_TYPE_APIKEY = "apikey";
    public static final String AUTH_TYPE_NONE = "none";

    // COLORS
    public static final String COLOR_RESET = "\033[0m";
    public static final String COLOR_RED = "\033[0;31m";
    public static final String COLOR_GREEN = "\033[0;32m";
    public static final String COLOR_YELLOW = "\033[0;33m";
    public static final String COLOR_BLUE = "\033[0;34m";
    public static final String COLOR_PURPLE = "\033[0;35m";
    public static final String COLOR_CYAN = "\033[0;36m";
    public static final String COLOR_WHITE = "\033[0;37m";
    public static final String COLOR_BLACK_BOLD = "\033[1;30m";

}
