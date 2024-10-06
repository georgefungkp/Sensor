package org.george_fung.com.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LoadYamlIntoMapTest {

    private ObjectMapper mockObjectMapper;

    @BeforeEach
    void setUp() {
        // Mock the ObjectMapper
        mockObjectMapper = Mockito.spy(new ObjectMapper(new YAMLFactory()));
        LoadYamlIntoMap.mapper = mockObjectMapper;
    }

    @Test
    void testGetMapWithValidYamlFile() throws IOException {
        // Simulate a valid YAML file input
        File mockFile = Mockito.mock(File.class);

        // Simulate the parsed YAML content
        Map<String, Object> yamlContent = new HashMap<>();
        yamlContent.put("sensors", Map.of("temperature", 30, "humidity", 50));

        // Stub the ObjectMapper to return the yamlContent map
        Mockito.doReturn(yamlContent).when(mockObjectMapper).readValue(mockFile, Map.class);

        // Invoke the method
        Map<String, Object> resultMap = LoadYamlIntoMap.getMap(mockFile);

        // Verify that the resultMap contains the expected flattened values
        assertTrue(resultMap.containsKey("sensors.temperature"));
        assertTrue(resultMap.containsKey("sensors.humidity"));
        assertEquals(30, resultMap.get("sensors.temperature"));
        assertEquals(50, resultMap.get("sensors.humidity"));
    }

    @Test
    void testGetMapWithEmptyYamlFile() throws IOException {
        // Simulate an empty YAML file
        File mockFile = Mockito.mock(File.class);

        // Stub the ObjectMapper to return an empty map
        Mockito.doReturn(new HashMap<>()).when(mockObjectMapper).readValue(mockFile, Map.class);

        // Invoke the method
        Map<String, Object> resultMap = LoadYamlIntoMap.getMap(mockFile);

        // Verify that the result map is empty
        assertTrue(resultMap.isEmpty());
    }

    @Test
    void testFlattenedYamlWithNestedMap() {
        // Input: Nested map structure
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("level1", Map.of("level2", Map.of("key", "value")));

        // Output: Flattened map
        Map<String, Object> resultMap = new HashMap<>();

        // Call the private method through reflection or indirectly
        LoadYamlIntoMap.flattenedYaml(inputMap, resultMap, null);

        // Verify that the nested map is correctly flattened
        assertTrue(resultMap.containsKey("level1.level2.key"));
        assertEquals("value", resultMap.get("level1.level2.key"));
    }

    @Test
    void testFlattenedYamlWithListOfMaps() {
        // Input: List of maps structure
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("listOfMaps", List.of(Map.of("key1", "value1"), Map.of("key2", "value2")));

        // Output: Flattened map
        Map<String, Object> resultMap = new HashMap<>();

        // Call the private method through reflection or indirectly
        LoadYamlIntoMap.flattenedYaml(inputMap, resultMap, null);

        // Verify that the list of maps is correctly flattened
        assertTrue(resultMap.containsKey("listOfMaps.[0].key1"));
        assertTrue(resultMap.containsKey("listOfMaps.[0].key2"));
        assertEquals("value1", resultMap.get("listOfMaps.[0].key1"));
        assertEquals("value2", resultMap.get("listOfMaps.[0].key2"));
    }

    @Test
    void testFlattenedYamlWithEmptyMap() {
        // Input: Empty map
        Map<String, Object> inputMap = new HashMap<>();

        // Output: Empty map
        Map<String, Object> resultMap = new HashMap<>();

        // Call the private method through reflection or indirectly
        LoadYamlIntoMap.flattenedYaml(inputMap, resultMap, null);

        // Verify that the result map is empty
        assertTrue(resultMap.isEmpty());
    }

    @Test
    void testMainMethodWithValidYamlFile() throws Exception {
        // Test the main method with a valid YAML file
        // Mock the file and setup valid output
        URL url = LoadYamlIntoMap.class.getClassLoader().getResource("dev_config.yaml");
        assertNotNull(url);
        File file = new File(url.toURI());

        Map<String, Object> resultMap = LoadYamlIntoMap.getMap(file);

        // Assert that the resultMap is not null
        assertNotNull(resultMap);
        assertFalse(resultMap.isEmpty()); // Ensure it's not empty (depending on your file)
    }
}
