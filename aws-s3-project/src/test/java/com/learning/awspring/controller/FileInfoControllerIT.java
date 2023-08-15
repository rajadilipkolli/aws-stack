package com.learning.awspring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.model.SignedUploadRequest;
import io.awspring.cloud.s3.S3Template;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class FileInfoControllerIT extends AbstractIntegrationTest {

    @Autowired private S3Template s3Template;

    @Test
    void downloadFileUsingSignedURL() throws Exception {
        // setUp data
        this.s3Template.store("testbucket", "junit.txt", "Hyderabad");

        this.mockMvc
                .perform(get("/s3/download/signed/{bucketName}/{name}", "testbucket", "junit.txt"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.url",
                                containsString(
                                        "/testbucket/junit.txt?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=")));
    }

    @Test
    void uploadFileUsingSignedURL() throws Exception {

        SignedUploadRequest signedUploadRequest =
                new SignedUploadRequest(
                        "testBucket", "junit.txt", "text/plain", Map.of("testkey", "testValue"));

        this.mockMvc
                .perform(
                        post("/s3/upload/signed/")
                                .content(this.objectMapper.writeValueAsString(signedUploadRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.url",
                                containsString(
                                        "/testBucket/junit.txt?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=")));
    }
}
