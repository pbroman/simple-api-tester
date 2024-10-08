package dev.pbroman.simple.api.tester.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pbroman.simple.api.tester.api.ConfigLoader;
import dev.pbroman.simple.api.tester.records.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

/**
 * A config loader using SnakeYaml rather than Jackson to read the yaml file,
 * since Jackson cannot deal with YAML anchors and references out-of-the-box.<br/>
 *
 * See <a href="https://github.com/FasterXML/jackson-dataformats-text/issues/98">FasterXML issue 98</a>
 */
@Component
public class SnakeYamlConfigLoader implements ConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(SnakeYamlConfigLoader.class);

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    public SnakeYamlConfigLoader(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.resourceLoader = resourceLoader;
    }

    public TestSuite loadTestSuite(String location) throws IOException {
        log.debug("Loading test suite from {}", location);
        var jsonString = locationToYaml(location);



        return objectMapper.readValue(jsonString, TestSuite.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> loadEnv(String location) throws IOException {
        log.debug("Loading env from {}", location);
        var jsonString = locationToYaml(location);
        return objectMapper.readValue(jsonString, Map.class);
    }

    private String locationToYaml(String location) throws IOException {
        var inputStream = resourceLoader.getResource(location).getInputStream();
        var yaml = new Yaml().load(inputStream);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(yaml);
    }
}
