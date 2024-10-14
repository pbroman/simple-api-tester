package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.exception.ValidationException;
import dev.pbroman.simple.api.tester.records.Request;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.ConditionResolver;
import dev.pbroman.simple.api.tester.util.Interpolation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static dev.pbroman.simple.api.tester.util.Constants.*;

public class ValidationRequestProcessor implements RequestProcessor {

    private static final Logger validationLog = LoggerFactory.getLogger(VALIDATION_LOGGER);

    public static final String VALIDATION_ERRORS = "_validation_errors";
    public static final String VALIDATION_WARNINGS = "_validation_warnings";

    @Override
    public void processRequest(Request request, RuntimeData runtimeData) {

        // Mock values
        runtimeData.responseVars().put(JSON, new JSONObject());
        runtimeData.responseVars().put(STATUS_CODE, 200);

        if (request.skipCondition() != null) {
            try {
                ConditionResolver.resolve(request.skipCondition().interpolated(runtimeData));
            } catch (ValidationException e) {
                addValidationWarning(runtimeData, e.getMessage());
            } catch (Exception e) {
                addValidationError(runtimeData, e.getMessage());
            }
        }

        request.responseHandling().assertions().forEach(condition -> {
            try {
                var interpolatedCondition = condition.interpolated(runtimeData);
                ConditionResolver.resolve(interpolatedCondition);
            } catch (ValidationException e) {
                addValidationWarning(runtimeData, e.getMessage());
            } catch (Exception e) {
                addValidationError(runtimeData, e.getMessage());
            }
        });

        request.responseHandling().setVars().forEach((key, value) -> {
            try {
                runtimeData.vars().put(key, Interpolation.interpolate(value, runtimeData));
            } catch (ValidationException e) {
                addValidationWarning(runtimeData, e.getMessage());
            } catch (Exception e) {
                addValidationError(runtimeData, e.getMessage());
            }
        });

        if (request.flowControl() != null) {
            try {
                var flowControl = request.flowControl().interpolated(runtimeData);
                if (flowControl.repeatUntil() != null) {
                    ConditionResolver.resolve(flowControl.repeatUntil().condition());
                }
            } catch (ValidationException e) {
                addValidationWarning(runtimeData, e.getMessage());
            } catch (Exception e) {
                addValidationError(runtimeData, e.getMessage());
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addValidationWarning(RuntimeData runtimeData, String message) {
        validationLog.warn(message);
        runtimeData.vars().computeIfAbsent(VALIDATION_WARNINGS, k -> new ArrayList<>());
        ((List) runtimeData.vars().get(VALIDATION_WARNINGS)).add(message);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addValidationError(RuntimeData runtimeData, String message) {
        validationLog.error(message);
        runtimeData.vars().computeIfAbsent(VALIDATION_ERRORS, k -> new ArrayList<>());
        ((List) runtimeData.vars().get(VALIDATION_ERRORS)).add(message);
    }
}
