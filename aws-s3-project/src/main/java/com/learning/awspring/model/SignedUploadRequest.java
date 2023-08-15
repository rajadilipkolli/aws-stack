package com.learning.awspring.model;

import java.io.Serializable;
import java.util.Map;

public record SignedUploadRequest(
        String bucketName, String fileName, String contentType, Map<String, String> metadata)
        implements Serializable {}
