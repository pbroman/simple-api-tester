package dev.pbroman.simple.api.tester.control;

import dev.pbroman.simple.api.tester.api.RequestProcessor;
import dev.pbroman.simple.api.tester.exception.ValidationException;
import dev.pbroman.simple.api.tester.records.Condition;
import dev.pbroman.simple.api.tester.records.Request;
import dev.pbroman.simple.api.tester.records.result.Validation;
import dev.pbroman.simple.api.tester.records.result.ValidationType;
import dev.pbroman.simple.api.tester.records.runtime.RuntimeData;
import dev.pbroman.simple.api.tester.util.ConditionResolver;
import dev.pbroman.simple.api.tester.util.Interpolation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import static dev.pbroman.simple.api.tester.util.Constants.*;

public class ValidationRequestProcessor implements RequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(ValidationRequestProcessor.class);

    public static final String CONDITION_RESOLVER_COMPONENT = "ConditionResolver";
    public static final String CONDITION_INTERPOLATION_COMPONENT = "ConditionInterpolation";
    public static final String SET_VARS_COMPONENT = "SetVarsValueInterpolation";
    public static final String FLOW_CONTROL_INTERPOLATION_COMPONENT = "FlowControlInterpolation";

    @Override
    public void processRequest(Request request, RuntimeData runtimeData) {

        // Mock values
        runtimeData.responseVars().put(JSON, new JSONObject());
        runtimeData.responseVars().put(STATUS_CODE, 200);

        if (request.skipCondition() != null) {
            var interpolatedCondition = validateConditionInterpolation(request, request.skipCondition(), runtimeData);
            validateConditionResolving(request, interpolatedCondition);
        }

        if (request.requestDefinition() != null) {
            try {
                request.requestDefinition().interpolated(runtimeData);
                new URI(request.requestDefinition().url()).toURL();
            } catch (ValidationException e) {
                request.validations().add(new Validation("RequestDefinitionInterpolation", request.requestDefinition().url(), e.getMessage(), e.getValidationType()));
            } catch (Exception e) {
                request.validations().add(new Validation("RequestDefinitionInterpolation", request.requestDefinition().url(), e.getMessage(), ValidationType.FAIL));
            }
        }

        if (request.responseHandling() != null) {
            request.responseHandling().assertions().forEach(condition -> {
                var interpolatedCondition = validateConditionInterpolation(request, condition, runtimeData);
                validateConditionResolving(request, interpolatedCondition);
            });
            request.responseHandling().setVars().forEach((key, value) -> {
                try {
                    runtimeData.vars().put(key, Interpolation.interpolate(value, runtimeData));
                } catch (ValidationException e) {
                    request.validations().add(new Validation(SET_VARS_COMPONENT, value, e.getMessage(), e.getValidationType()));
                } catch (Exception e) {
                    request.validations().add(new Validation(SET_VARS_COMPONENT, value, e.getMessage(), ValidationType.FAIL));
                }
            });
        }


        if (request.flowControl() != null) {
            try {
                var flowControl = request.flowControl().interpolated(runtimeData);
                if (flowControl.repeatUntil() != null) {
                    validateConditionResolving(request, flowControl.repeatUntil().condition());
                }
            } catch (ValidationException e) {
                request.validations().add(new Validation(FLOW_CONTROL_INTERPOLATION_COMPONENT, "flow control", e.getMessage(), e.getValidationType()));
            } catch (Exception e) {
                request.validations().add(new Validation(FLOW_CONTROL_INTERPOLATION_COMPONENT, "flow control", e.getMessage(), ValidationType.FAIL));
            }
        }
    }

    private Condition validateConditionInterpolation(Request request, Condition condition, RuntimeData runtimeData) {
        try {
            return condition.interpolated(runtimeData);
        } catch (ValidationException e) {
            request.validations().add(new Validation(CONDITION_INTERPOLATION_COMPONENT, condition.getInstanceName(), e.getMessage(), e.getValidationType()));
        } catch (Exception e) {
            request.validations().add(new Validation(CONDITION_INTERPOLATION_COMPONENT, condition.getInstanceName(), e.getMessage(), ValidationType.FAIL));
        }
        return null;
    }

    private void validateConditionResolving(Request request, Condition condition) {
        if (condition == null) {
            return;
        }
        try {
            ConditionResolver.resolve(condition);
        } catch (ValidationException e) {
            request.validations().add(new Validation(CONDITION_RESOLVER_COMPONENT, condition.getInstanceName(), e.getMessage(), e.getValidationType()));
        } catch (Exception e) {
            request.validations().add(new Validation(CONDITION_RESOLVER_COMPONENT, condition.getInstanceName(), e.getMessage(), ValidationType.FAIL));
        }
    }
}
