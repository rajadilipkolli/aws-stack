package com.learning.awspring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FileInfoControllerIT extends AbstractIntegrationTest {

    @Autowired private S3Template s3Template;

    @Test
    void viewAllFromS3BucketUsingKey() throws Exception {
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
}
