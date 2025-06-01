package com.learning.awspring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application")
public record ApplicationProperties(
        String bucketName,
        boolean enableServerSideEncryption,
        String serverSideEncryptionAlgorithm,
        boolean enableVersioning) {

    public ApplicationProperties {
        // Set default values if not provided
        if (serverSideEncryptionAlgorithm == null) {
            serverSideEncryptionAlgorithm = "AES256"; // Default to AES256
        }
    }
}
