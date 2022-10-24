package com.learning.awspring.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageDeserializationUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String getMessageBodyAsJson(Object payload) {
        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting payload: " + payload, e);
        }
    }
}
