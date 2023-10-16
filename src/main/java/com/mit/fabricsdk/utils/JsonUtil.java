package com.mit.fabricsdk.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

public class JsonUtil {
    private JsonUtil() {
    }

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public static String toJson(Object obj) throws IOException {
        return mapper.writeValueAsString(obj);
    }

    public static <T> T toObject(String json, Class<T> valueType) throws IOException {
        return mapper.readValue(json, valueType);
    }

    public static <T> T toObject(String json, TypeReference<T> valueType) throws IOException {
        return mapper.readValue(json, valueType);
    }

    public static String toJsonQuietly(Object obj) {
        String result = null;
        try {
            if (obj instanceof String) {
                return (String) obj;
            }
            result = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            logger.error("Failed to serialize Object", ex);
        }
        return result;
    }

    public static <T> T toObjectQuietly(String json, Class<T> valueType) {
        T object = null;
        try {
            object = mapper.readValue(json, valueType);
        } catch (Exception ex) {
            logger.error("Failed to deserialize Object", ex);
        }
        return object;
    }

    public static JsonNode readValueAsTree(String content) throws IOException {
        return mapper.readTree(content);
    }

    //read file to object
    public static <T> T readObjectFromFile(String fileName, Class<T> valueType) throws IOException {
        return mapper.readValue(mapper.getClass().getResourceAsStream(fileName), valueType);
    }

    public static void replaceNodes(JsonNode origin, List<String> paths, JsonNode replaced) {
        paths.forEach(path -> replaceNode(origin, path.split("\\.")[0], path.split("\\.")[1], replaced));
    }

    private static void replaceNode(JsonNode origin, String parent, String child, JsonNode replaced) {
        visitNode(origin, parent, child, (n, c) -> ((ObjectNode) n).replace(c, replaced));
    }


    public static List<JsonNode> findNodes(JsonNode rootNode, String parent, String child) {
        List<JsonNode> nodes = Lists.newArrayList();
        visitNode(rootNode, parent, child, (n, c) -> nodes.add(n.get(c)));
        return nodes;
    }

    private static void visitNode(JsonNode origin, String parent, String child, BiConsumer<JsonNode, String> consumer) {
        List<JsonNode> jsonNodes = origin.findValues(parent);
        jsonNodes.forEach(node -> {
            if (node instanceof ObjectNode) {
                consumer.accept(node, child);
            }
            if (node instanceof ArrayNode) {
                ArrayNode arrayNode = (ArrayNode) node;
                for (JsonNode jsonNode : arrayNode) {
                    if (jsonNode instanceof ObjectNode) {
                        consumer.accept(jsonNode, child);
                    }
                }
            }
        });
    }


}
