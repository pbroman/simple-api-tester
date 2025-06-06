package dev.pbroman.brat.core.control;

import dev.pbroman.brat.core.api.RequestProcessor;
import dev.pbroman.brat.core.exception.ValidationException;
import dev.pbroman.brat.core.records.Condition;
import dev.pbroman.brat.core.records.Request;
import dev.pbroman.brat.core.records.result.Validation;
import dev.pbroman.brat.core.records.result.ValidationType;
import dev.pbroman.brat.core.records.runtime.RuntimeData;
import dev.pbroman.brat.core.util.ConditionResolver;
import dev.pbroman.brat.core.util.Interpolation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import static dev.pbroman.brat.core.util.Constants.*;

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
            validateConditionResolving(runtimeData, interpolatedCondition);
        }

        if (request.requestDefinition() != null) {
            try {
                var requestDef = request.requestDefinition().interpolated(runtimeData);
                new URI(requestDef.url()).toURL();
            } catch (ValidationException e) {
                runtimeData.validations().add(new Validation("RequestDefinitionInterpolation", request.requestDefinition().url(), e.getMessage(), e.getValidationType()));
            } catch (Exception e) {
                runtimeData.validations().add(new Validation("RequestDefinitionInterpolation", request.requestDefinition().url(), e.getMessage(), ValidationType.FAIL));
            }
        }

        if (request.responseHandling() != null) {
            request.responseHandling().assertions().forEach(condition -> {
                var interpolatedCondition = validateConditionInterpolation(request, condition, runtimeData);
                validateConditionResolving(runtimeData, interpolatedCondition);
            });
            request.responseHandling().setVars().forEach((key, value) -> {
                try {
                    runtimeData.vars().put(key, Interpolation.interpolate(value, runtimeData));
                } catch (ValidationException e) {
                    runtimeData.validations().add(new Validation(SET_VARS_COMPONENT, value, e.getMessage(), e.getValidationType()));
                } catch (Exception e) {
                    runtimeData.validations().add(new Validation(SET_VARS_COMPONENT, value, e.getMessage(), ValidationType.FAIL));
                }
            });
        }


        if (request.flowControl() != null) {
            try {
                var flowControl = request.flowControl().interpolated(runtimeData);
                if (flowControl.repeatUntil() != null) {
                    validateConditionResolving(runtimeData, flowControl.repeatUntil().condition());
                }
            } catch (ValidationException e) {
                runtimeData.validations().add(new Validation(FLOW_CONTROL_INTERPOLATION_COMPONENT, "flow control", e.getMessage(), e.getValidationType()));
            } catch (Exception e) {
                runtimeData.validations().add(new Validation(FLOW_CONTROL_INTERPOLATION_COMPONENT, "flow control", e.getMessage(), ValidationType.FAIL));
            }
        }
    }

    private Condition validateConditionInterpolation(Request request, Condition condition, RuntimeData runtimeData) {
        try {
            return condition.interpolated(runtimeData);
        } catch (ValidationException e) {
            runtimeData.validations().add(new Validation(CONDITION_INTERPOLATION_COMPONENT, condition.getInstanceName(), e.getMessage(), e.getValidationType()));
        } catch (Exception e) {
            runtimeData.validations().add(new Validation(CONDITION_INTERPOLATION_COMPONENT, condition.getInstanceName(), e.getMessage(), ValidationType.FAIL));
        }
        return null;
    }

    private void validateConditionResolving(RuntimeData runtimeData, Condition condition) {
        if (condition == null) {
            return;
        }
        if (!condition.validate().isEmpty()) {
            // Condition already invalid, no need to try to resolve it as well
            return;
        }
        try {
            ConditionResolver.resolve(condition);
        } catch (ValidationException e) {
            runtimeData.validations().add(new Validation(CONDITION_RESOLVER_COMPONENT, condition.getInstanceName(), e.getMessage(), e.getValidationType()));
        } catch (Exception e) {
            runtimeData.validations().add(new Validation(CONDITION_RESOLVER_COMPONENT, condition.getInstanceName(), e.getMessage(), ValidationType.FAIL));
        }
    }
}
