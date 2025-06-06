package dev.pbroman.brat.core.util;

import dev.pbroman.brat.core.records.runtime.RuntimeData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static dev.pbroman.brat.core.util.Constants.BODY;
import static dev.pbroman.brat.core.util.Constants.HEADERS;
import static dev.pbroman.brat.core.util.Constants.JSON;
import static dev.pbroman.brat.core.util.Constants.STATUS_CODE;
import static org.junit.jupiter.api.Assertions.*;

class InterpolationTest {

    private RuntimeData runtimeData;

    @BeforeEach
    void init() throws JSONException {
        var constants = Map.of("foo", "bar");
        var env = Map.of("name", "dev", "baseUrl", "http://example.com");
        var variables = Map.of("var1", "value1", "var2", List.of());
        var responseBody = """
                {
                    "id": "123",
                    "array": [ { "test": "test" }, { "array2": [{}] } ],
                    "object": {}
                }
                """.strip();
        var responseVars = Map.of(
                STATUS_CODE, 200,
                HEADERS, Map.of("Content-Type", "application/json"),
                BODY, responseBody,
                JSON, new JSONObject(responseBody)
        );
        runtimeData = new RuntimeData(constants, env, variables, responseVars);
    }

    @Test
    void singleStringInterpolation() {
        testInterpolation("Hello ${constants.foo}!", "Hello bar!");
        testInterpolation("Hello ${env.name}!", "Hello dev!");
        testInterpolation("Bye ${vars.var1}!", "Bye value1!");
        testInterpolation("Hello ${response.statusCode}!", "Hello 200!");
        testInterpolation("${response.headers.Content-Type}", "application/json");
        testInterpolation("The id is ${response.json.id}", "The id is 123");
        testInterpolation("${response.body}", runtimeData.responseVars().get(BODY));
        testInterpolation("${response.json.array[0].test}", "test");
    }

    @Test
    void objectInterpolation() throws JSONException {
        testInterpolation("${response.json.array}", new JSONArray("[{\"test\":\"test\"},{\"array2\":[{}]}]"));
        testInterpolation("${response.json.array[0]}", new JSONObject("{\"test\":\"test\"}"));
        testInterpolation("${response.json.array[1].array2}", new JSONArray("[{}]"));
        testInterpolation("${response.json.array[1].array2[0]}", new JSONObject());
    }

    @Test
    void functionInterpolation() throws JSONException {
        testInterpolation("${response.json.array._length}", 2);
        testInterpolation("${response.json.array._isArray}", true);
        testInterpolation("${response.json.array._isString}", false);
        testInterpolation("${response.json.array._isObject}", false);
        testInterpolation("${response.json.object._isObject}", true);
        testInterpolation("${response.json.id._isString}", true);
        testInterpolation("${response.json.id._length}", 3);
        testInterpolation("${response.json.object._length}", 0);
        testInterpolation("${response.json.array[1].array2._length}", 1);
        testInterpolation("${response.json._length}", 3);
        testInterpolation("${response.json._isArray}", false);
        testInterpolation("${response.json._isString}", false);
        testInterpolation("${response.json._isObject}", true);
    }

    @Test
    void multipleStringInterpolation() {
        testInterpolation("Hello ${constants.foo} and ${vars.var1}!", "Hello bar and value1!");
        testInterpolation("${env.baseUrl}/foo/${vars.nonPresent}", "http://example.com/foo/");
    }

    @Test
    void emptyResponseJson() {
        // given
        var runtimeData = new RuntimeData(Map.of(), Map.of(), Map.of(), Map.of(JSON, new JSONObject()));

        // when
        var result = Interpolation.interpolate("${response.json}", runtimeData);

        // then
        assertEquals(JSONObject.class, result.getClass());
        assertEquals("{}", result.toString());
    }

    @Test
    void emptyArrayResponseJson() {
        // given
        var runtimeData = new RuntimeData(Map.of(), Map.of(), Map.of(), Map.of(JSON, new JSONArray()));

        // when
        var result = Interpolation.interpolate("${response.json}", runtimeData);

        // then
        assertEquals(JSONArray.class, result.getClass());
        assertEquals("[]", result.toString());
    }

    @Test
    void arrayResponseJson() {
        // given
        String jsonArray = "[{\"foo\": \"bar\"}]";
        var runtimeData = new RuntimeData(Map.of(), Map.of(), Map.of(), Map.of(JSON, new JSONArray(jsonArray)));

        // when
        var result = Interpolation.interpolate("${response.json[0].foo}", runtimeData);

        // then
        assertEquals(String.class, result.getClass());
        assertEquals("bar", result.toString());
    }

    @Test
    void deepArrayResponseJson() {
        // given
        String jsonArray = """
                [ {}, {}, {
                    "foo": "bar",
                    "object": {
                        "array": [ {}, {
                            "moo": "baa"
                        }]
                    }
                }]
            """;
        var runtimeData = new RuntimeData(Map.of(), Map.of(), Map.of(), Map.of(JSON, new JSONArray(jsonArray)));

        // when
        var result = Interpolation.interpolate("${response.json[2].object.array[1].moo}", runtimeData);

        // then
        assertEquals(String.class, result.getClass());
        assertEquals("baa", result.toString());
    }

    @Test
    void interpolateNullInput() {
        assertNull(Interpolation.interpolate(null, runtimeData));
    }

    @Test
    void interpolateNonStringInput() {
        // given
        var list = List.of();

        // when
        var result = Interpolation.interpolate(list, runtimeData);

        // then
        assertEquals(list, result);
    }

    @Test
    void interpolateNonExistingJsonPath() {
        // when
        var result = Interpolation.interpolate("${response.json.non.existing}", runtimeData);

        // then
        assertNull(result);
    }

    @Test
    void interpolateMap() {
        // given
        var map = Map.of("foo", "${constants.foo}", "bar", "${env.name}");

        // when
        var result = Interpolation.interpolateMap(map, runtimeData);

        // then
        assertEquals("bar", result.get("foo"));
        assertEquals("dev", result.get("bar"));
    }

    @Test
    void interpolateMapNullInput() {
        assertNull(Interpolation.interpolate(null, runtimeData));
    }

    @Test
    void invalidResponseJson() {
        // given
        var runtimeData = new RuntimeData(Map.of(), Map.of(), Map.of(), Map.of(JSON, "invalid"));

        // then
        assertThrows(IllegalArgumentException.class,
                () -> Interpolation.interpolate("${response.json}", runtimeData)
        );
    }

    @Test
    void unhappyPaths() {
        assertThrows(IllegalArgumentException.class,
                () -> Interpolation.interpolate("${constants.foo.moo}", runtimeData)
        );
        assertThrows(IllegalArgumentException.class,
                () -> Interpolation.interpolate("${env}", runtimeData)
        );
        assertThrows(IllegalArgumentException.class,
                () -> Interpolation.interpolate("${response.statusCode.foo}", runtimeData)
        );
        assertThrows(IllegalArgumentException.class,
                () -> Interpolation.interpolate("${status}", runtimeData)
        );
        assertThrows(IllegalArgumentException.class,
                () -> Interpolation.interpolate("${response.json.id._isBollocks}", runtimeData)
        );
    }

    private void testInterpolation(String input, Object expected) {
        // when
        var result = Interpolation.interpolate(input, runtimeData);

        // then
        assertEquals(expected.getClass(), result.getClass());
        assertEquals(expected.toString(), result.toString());
    }
}