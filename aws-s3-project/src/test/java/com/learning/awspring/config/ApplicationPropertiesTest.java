package com.learning.awspring.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ApplicationPropertiesTest {

    @Test
    void constructor_shouldUseDefaultEncryptionAlgorithm_whenNullIsProvided() {
        // Arrange & Act
        ApplicationProperties props = new ApplicationProperties("test-bucket", true, null, false);

        // Assert
        assertEquals("test-bucket", props.bucketName());
        assertTrue(props.enableServerSideEncryption());
        assertEquals("AES256", props.serverSideEncryptionAlgorithm());
        assertFalse(props.enableVersioning());
    }

    @Test
    void constructor_shouldUseProvidedValues() {
        // Arrange & Act
        ApplicationProperties props =
                new ApplicationProperties("test-bucket", true, "aws:kms", true);

        // Assert
        assertEquals("test-bucket", props.bucketName());
        assertTrue(props.enableServerSideEncryption());
        assertEquals("aws:kms", props.serverSideEncryptionAlgorithm());
        assertTrue(props.enableVersioning());
    }
}
