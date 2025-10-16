package com.learning.awspring.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class MessageDeserializationUtil {

    private MessageDeserializationUtil() {
        // private constructor to prevent instantiation
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String getMessageBodyAsJson(Object payload) {
        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting payload: " + payload, e);
        }
    }
}
