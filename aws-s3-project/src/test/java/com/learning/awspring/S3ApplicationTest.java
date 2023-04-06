package com.learning.awspring;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.repository.FileInfoRepository;

import io.awspring.cloud.s3.S3Template;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

class S3ApplicationTest extends AbstractIntegrationTest {

    @Autowired FileInfoRepository fileInfoRepository;
    @Autowired private S3Template s3Template;

    @Autowired private S3Client s3Client;

    @Test
    void contextLoads() {
        assertThat(fileInfoRepository.existsByFileName("fileName")).isFalse();
    }

    @Test
    void s3Tests() {
        assertThat(this.s3Client.listBuckets().buckets()).hasSize(1);

        this.s3Template.store("testbucket", "junit.txt", "Las Vegas");
        ListObjectsV2Response listConferencesObjectsV2Response =
                this.s3Client.listObjectsV2(
                        ListObjectsV2Request.builder().bucket("testbucket").build());
        assertThat(listConferencesObjectsV2Response.contents()).hasSize(1);

        this.s3Template.createBucket("talks");
        assertThat(this.s3Client.listBuckets().buckets()).hasSize(2);

        this.s3Template.store(
                "talks", "Adopting Testcontainers for local development.txt", "Hello Junit");
        ListObjectsV2Response listTalksObjectsV2Response =
                this.s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket("talks").build());
        assertThat(listTalksObjectsV2Response.contents()).hasSize(1);
    }
}
