package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class JsonChildNodeCreator {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File("C:\\Users\\asus\\Downloads\\demo\\APIs_for_maven_project\\src\\main\\resources\\sampleJson.json");

        try {
            // Read the JSON from the file
            JsonNode rootNode = mapper.readTree(jsonFile);

            // Sample input path and value
            String inputPath = "details.address.street[0].Lucknow[0].balle.suiii";

            String childNodeValue = "123 Main St";

            // Update the JSON
            JsonNode updatedNode = addChildNode(rootNode, inputPath, childNodeValue, mapper);

            // Write the updated JSON back to the file
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, updatedNode);

            // Print the updated JSON to the console
            String updatedJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(updatedNode);
            System.out.println(updatedJsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonNode addChildNode(JsonNode rootNode, String path, String value, ObjectMapper mapper) {
        String[] keys = path.split("\\.");
        JsonNode currentNode = rootNode;

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            boolean isArray = key.contains("[");
            String arrayKey = null;
            int index = -1;

            if (isArray) {
                arrayKey = key.substring(0, key.indexOf("["));
                index = Integer.parseInt(key.substring(key.indexOf("[") + 1, key.indexOf("]")));
                key = arrayKey;
            }

            if (i == keys.length - 1) { // Last key, set the value
                if (isArray) {
                    ArrayNode arrayNode;
                    if (currentNode.has(arrayKey)) {
                        arrayNode = (ArrayNode) currentNode.get(arrayKey);
                    } else {
                        arrayNode = mapper.createArrayNode();
                        ((ObjectNode) currentNode).set(arrayKey, arrayNode);
                    }

                    while (arrayNode.size() <= index) {
                        arrayNode.add(mapper.createObjectNode());
                    }
                    ((ObjectNode) arrayNode.get(index)).put(keys[i].substring(keys[i].indexOf("]") + 1), value);
                } else {
                    ((ObjectNode) currentNode).put(key, value);
                }
            } else { // Intermediate keys
                if (isArray) {
                    ArrayNode arrayNode;
                    if (currentNode.has(arrayKey)) {
                        arrayNode = (ArrayNode) currentNode.get(arrayKey);
                    } else {
                        arrayNode = mapper.createArrayNode();
                        ((ObjectNode) currentNode).set(arrayKey, arrayNode);
                    }

                    while (arrayNode.size() <= index) {
                        arrayNode.add(mapper.createObjectNode());
                    }
                    currentNode = arrayNode.get(index);
                } else {
                    if (!currentNode.has(key)) {
                        ((ObjectNode) currentNode).set(key, mapper.createObjectNode());
                    }
                    currentNode = currentNode.get(key);
                }
            }
        }
        return rootNode;
    }
}
