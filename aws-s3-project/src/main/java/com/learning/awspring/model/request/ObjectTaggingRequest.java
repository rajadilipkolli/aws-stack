package com.learning.awspring.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

public record ObjectTaggingRequest(@NotBlank String fileName, @NotEmpty Map<String, String> tags)
        implements Serializable {}
