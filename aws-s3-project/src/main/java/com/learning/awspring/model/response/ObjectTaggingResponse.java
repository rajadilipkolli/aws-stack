package com.learning.awspring.model.response;

import java.io.Serializable;
import java.util.Map;

public record ObjectTaggingResponse(String fileName, Map<String, String> tags, boolean success)
        implements Serializable {}
