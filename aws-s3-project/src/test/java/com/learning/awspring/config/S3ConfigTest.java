package com.learning.awspring.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@ExtendWith(MockitoExtension.class)
class S3ConfigTest {

    @Mock private ApplicationProperties applicationProperties;

    @Mock private S3Client s3Client;

    @Mock private S3Template s3Template;

    @InjectMocks private S3Config s3Config;

    @Test
    void setupBucketOnStartup_shouldCreateBucketIfNotExists() {
        // Arrange
        String bucketName = "testBucket";
        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(applicationProperties.enableVersioning()).thenReturn(false);
        when(s3Template.bucketExists(bucketName)).thenReturn(false);
        when(s3Template.createBucket(bucketName)).thenReturn("location");

        // Act
        s3Config.setupBucketOnStartup();

        // Assert
        verify(s3Template).bucketExists(bucketName);
        verify(s3Template).createBucket(bucketName);
        verify(applicationProperties).enableVersioning();
    }

    @Test
    void setupBucketOnStartup_shouldSkipCreationIfBucketExists() {
        // Arrange
        String bucketName = "testBucket";
        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(applicationProperties.enableVersioning()).thenReturn(false);
        when(s3Template.bucketExists(bucketName)).thenReturn(true);

        // Act
        s3Config.setupBucketOnStartup();

        // Assert
        verify(s3Template).bucketExists(bucketName);
        verify(s3Template, never()).createBucket(any());
        verify(applicationProperties).enableVersioning();
    }

    @Test
    @Disabled("till mocking of PutBucketVersioningResponse is fixed")
    void setupBucketOnStartup_shouldEnableVersioningIfConfigured() {
        // Arrange
        String bucketName = "testBucket";
        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(applicationProperties.enableVersioning()).thenReturn(true);
        when(s3Template.bucketExists(bucketName)).thenReturn(true);

        // Setup for versioning
        GetBucketVersioningResponse versioningResponse =
                GetBucketVersioningResponse.builder()
                        .status(BucketVersioningStatus.SUSPENDED)
                        .build();
        when(s3Client.getBucketVersioning(any(GetBucketVersioningRequest.class)))
                .thenReturn(versioningResponse);
        when(s3Client.putBucketVersioning(any(PutBucketVersioningRequest.class)))
                .thenReturn(PutBucketVersioningResponse.builder().build());

        // Act
        s3Config.setupBucketOnStartup();

        // Assert
        verify(s3Template).bucketExists(bucketName);
        verify(s3Client).getBucketVersioning(any(GetBucketVersioningRequest.class));
        verify(s3Client).putBucketVersioning(any(PutBucketVersioningRequest.class));
    }

    @Test
    void setupBucketOnStartup_shouldSkipVersioningIfAlreadyEnabled() {
        // Arrange
        String bucketName = "testBucket";
        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(applicationProperties.enableVersioning()).thenReturn(true);
        when(s3Template.bucketExists(bucketName)).thenReturn(true);

        // Setup for versioning already enabled
        GetBucketVersioningResponse versioningResponse =
                GetBucketVersioningResponse.builder()
                        .status(BucketVersioningStatus.ENABLED)
                        .build();
        when(s3Client.getBucketVersioning(any(GetBucketVersioningRequest.class)))
                .thenReturn(versioningResponse);

        // Act
        s3Config.setupBucketOnStartup();

        // Assert
        verify(s3Template).bucketExists(bucketName);
        verify(s3Client).getBucketVersioning(any(GetBucketVersioningRequest.class));
        verify(s3Client, never()).putBucketVersioning(any(PutBucketVersioningRequest.class));
    }
}
