package com.learning.awspring.config;

import io.awspring.cloud.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Configuration
public class S3Config {

    private static final Logger log = LoggerFactory.getLogger(S3Config.class);

    private final ApplicationProperties applicationProperties;
    private final S3Template s3Template;
    private final S3Client s3Client;

    public S3Config(
            ApplicationProperties applicationProperties, S3Template s3Template, S3Client s3Client) {
        this.applicationProperties = applicationProperties;
        this.s3Template = s3Template;
        this.s3Client = s3Client;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setupBucketOnStartup() {
        String bucketName = applicationProperties.bucketName();

        // Create bucket if it doesn't exist
        if (!s3Template.bucketExists(bucketName)) {
            log.info("Creating S3 bucket: {}", bucketName);
            s3Template.createBucket(bucketName);
        }

        // Configure versioning if needed
        if (applicationProperties.enableVersioning()) {
            configureVersioning(bucketName);
        }
    }

    private void configureVersioning(String bucketName) {
        // Check current versioning status
        GetBucketVersioningResponse versioningResponse =
                s3Client.getBucketVersioning(
                        GetBucketVersioningRequest.builder().bucket(bucketName).build());

        // If versioning is not enabled, enable it
        if (!(BucketVersioningStatus.ENABLED == versioningResponse.status())) {
            log.info("Enabling versioning for bucket: {}", bucketName);

            PutBucketVersioningResponse putBucketVersioningResponse =
                    s3Client.putBucketVersioning(
                            PutBucketVersioningRequest.builder()
                                    .bucket(bucketName)
                                    .versioningConfiguration(
                                            VersioningConfiguration.builder()
                                                    .status(BucketVersioningStatus.ENABLED)
                                                    .build())
                                    .build());

            log.info(
                    "Versioning enabling status for bucket: {} is {}",
                    bucketName,
                    putBucketVersioningResponse.sdkHttpResponse().isSuccessful());
        } else {
            log.info("Versioning is already enabled for bucket: {}", bucketName);
        }
    }
}
