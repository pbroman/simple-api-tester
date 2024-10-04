package dev.pbroman.simple.api.tester.util;

import dev.pbroman.simple.api.tester.records.Condition;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ConditionResolver {

    public static final String NULL = "null";
    public static final String EMPTY = "empty";
    public static final String BLANK = "blank";
    public static final String EQUALS = "equals";
    public static final String EQUALS_IGNORE_CASE = "equalsignorecase";
    public static final String CONTAINS = "contains";
    public static final String STARTS_WITH = "startswith";
    public static final String ENDS_WITH = "endswith";
    public static final String MATCHES = "matches";

    public static final String IS_TRUE = "istrue";
    public static final String IS_FALSE = "isfalse";

    public static final String EQUAL_TO = "=";
    public static final String GREATER_THAN_OR_EQUAL = ">=";
    public static final String LESS_THAN_OR_EQUAL = "<=";
    public static final String GREATER_THAN = ">";
    public static final String LESS_THAN = "<";

    public static final String NEGATION_REGEX = "^(not|!)(.*)";
    public static final Pattern NEGATION_PATTERN = Pattern.compile(NEGATION_REGEX);

    public static final List<String> STRING_OPERATIONS = List.of(
            NULL, EMPTY, EQUALS, CONTAINS, STARTS_WITH, ENDS_WITH, MATCHES, EQUALS_IGNORE_CASE, BLANK
    );
    public static final List<String> BOOLEAN_OPERATIONS = List.of(
            IS_TRUE, IS_FALSE
    );
    public static final List<String> NUMBER_OPERATIONS = List.of(
            EQUAL_TO, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN, LESS_THAN
    );

    /**
     * Resolves a condition by comparing the value with the other value using the operation.
     *
     * @param condition - the condition to resolve
     * @return true or false
     * @throws IllegalArgumentException if the operation or the combination of operation and values is not supported
     */
    public static boolean resolve(Condition condition) {
        if (condition.value() == null) {
            return NULL.equals(condition.operation());
        }

        var operation = condition.operation().toLowerCase().trim();
        var negate = false;
        var matches = NEGATION_PATTERN.matcher(operation);
        if (matches.find()) {
            operation = matches.group(2);
            negate = true;
        }

        if (condition.other() == null && !List.of(NULL, EMPTY, BLANK, IS_TRUE, IS_FALSE).contains(operation)) {
            throw new IllegalArgumentException("other value may not be null for operation: " + operation);
        }

        try {
            if (STRING_OPERATIONS.contains(operation)) {
                return negate != resolveStringCondition(operation, convertToString(condition.other()), convertToString(condition.value()));
            }
            if (NUMBER_OPERATIONS.contains(operation)) {
                return negate != resolveDoubleCondition(operation, convertToDouble(condition.other()), convertToDouble(condition.value()));
            }
            if (BOOLEAN_OPERATIONS.contains(operation)) {
                return resolveBooleanCondition(operation, convertToBoolean(condition.value()));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported combination of conditional operation '" + operation +
                    "' with values '" + condition.value() + "' and '" + condition.other(), e);
        }
        throw new IllegalArgumentException("Unsupported conditional operation: " + operation);
    }

    private static boolean resolveStringCondition(String operation, String other, String value) {
        return switch (operation) {
            case NULL -> false;
            case EQUALS -> value.equals(other);
            case EQUALS_IGNORE_CASE -> value.equalsIgnoreCase(other);
            case BLANK -> value.isBlank();
            case EMPTY -> value.isEmpty();
            case CONTAINS -> value.contains(Objects.requireNonNull(other));
            case STARTS_WITH -> value.startsWith(Objects.requireNonNull(other));
            case ENDS_WITH -> value.endsWith(Objects.requireNonNull(other));
            case MATCHES -> value.matches(Objects.requireNonNull(other));
            default -> throw new IllegalArgumentException("Unsupported string condition operation: " + operation);
        };
    }

    private static boolean resolveBooleanCondition(String operation, Boolean value) {
        return switch (operation) {
            case IS_TRUE -> value;
            case IS_FALSE -> !value;
            default -> throw new IllegalArgumentException("Unsupported boolean operation: " + operation + " with value value: " + value);
        };
    }

    private static boolean resolveDoubleCondition(String operation, Double other, Double value) {
        return switch (operation) {
            case EQUAL_TO -> value.equals(other);
            case GREATER_THAN_OR_EQUAL -> value.compareTo(other) >= 0;
            case LESS_THAN_OR_EQUAL -> value.compareTo(other) <= 0;
            case GREATER_THAN -> value.compareTo(other) > 0;
            case LESS_THAN -> value.compareTo(other) < 0;
            default -> throw new IllegalArgumentException("Unsupported number operation: " + operation + " with values: " + value);
        };
    }

    private static String convertToString(Object value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case String s -> s;
            case Integer i -> i.toString();
            case Double d -> d.toString();
            case Boolean b -> b.toString();
            default -> null;
        };
    }

    private static Double convertToDouble(Object value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case Double d -> d;
            case String s -> Double.parseDouble(s);
            case Integer i -> Double.valueOf(i);
            default -> throw new IllegalArgumentException("Unsupported number value: " + value);
        };
    }

    private static Boolean convertToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case Boolean b -> b;
            case String s -> Boolean.valueOf(s);
            default -> null;
        };
    }

}
