package com.learning.awspring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.model.SignedUploadRequest;
import io.awspring.cloud.s3.S3Template;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

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
                new SignedUploadRequest("testbucket", Map.of("testkey", "testValue"));

        MockMultipartFile multipartFile =
                new MockMultipartFile(
                        "file",
                        "junit.txt",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello, World!".getBytes());
        MockMultipartFile metadata =
                new MockMultipartFile(
                        "json",
                        "json",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(signedUploadRequest));
        this.mockMvc
                .perform(
                        multipart("/s3/upload/signed/")
                                .file(multipartFile)
                                .file(metadata)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("File uploaded successfully!")));
    }
}
