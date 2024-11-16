package dev.pbroman.simple.api.tester.records;

import dev.pbroman.simple.api.tester.api.ConfigRecord;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.result.ValidationType;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.Interpolation;

import java.util.ArrayList;
import java.util.List;

import static dev.pbroman.simple.api.tester.util.Constants.AUTH_TYPE_APIKEY;
import static dev.pbroman.simple.api.tester.util.Constants.AUTH_TYPE_BASIC;
import static dev.pbroman.simple.api.tester.util.Constants.AUTH_TYPE_BEARER;
import static dev.pbroman.simple.api.tester.util.Constants.AUTH_TYPE_NONE;

public record Auth(String type, String username, String password, String token) implements ConfigRecord {

    public static final List<String> AUTH_TYPES = List.of(AUTH_TYPE_NONE, AUTH_TYPE_BASIC, AUTH_TYPE_BEARER, AUTH_TYPE_APIKEY);

    @Override
    public List<Validation> validate() {
        var validations = new ArrayList<Validation>();
        if (type == null) {
            validations.add(validation("Auth type cannot be null", ValidationType.FAIL));
        } else if (!AUTH_TYPES.contains(type)) {
            validations.add(validation("Auth type must be one of 'none', 'basic', 'bearer', or 'apikey', got: '" + type + "'", ValidationType.FAIL));
        } else {
            var authType = type.trim().toLowerCase();
            switch (authType) {
                case AUTH_TYPE_BASIC -> {
                    if (username == null) {
                        validations.add(validation("Username cannot be null for basic auth", ValidationType.FAIL));
                    }
                    if (password == null) {
                        validations.add(validation("Password cannot be null for basic auth", ValidationType.FAIL));
                    }
                }
                case AUTH_TYPE_BEARER -> {
                    if (token == null) {
                        validations.add(validation("Token cannot be null for bearer auth", ValidationType.FAIL));
                    }
                }
                case AUTH_TYPE_APIKEY -> {
                    if (token == null) {
                        validations.add(validation("Token cannot be null for apikey auth", ValidationType.FAIL));
                    }
                }
            }
        }
        return validations;
    }

    private Validation validation(String message, ValidationType validationType) {
        return new Validation(this.getClass().getSimpleName(), type, message, validationType);
    }

    @Override
    public Auth interpolated(RuntimeData runtimeData) {
        var uname = Interpolation.interpolate(username, runtimeData);
        var pw = Interpolation.interpolate(password, runtimeData);
        var itoken = Interpolation.interpolate(token, runtimeData);
        uname = uname != null ? uname.toString(): null;
        pw = pw != null ? pw.toString(): null;
        itoken = itoken != null ? itoken.toString(): null;
        return new Auth(type, (String) uname, (String) pw, (String) itoken);
    }
}
