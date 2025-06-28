package com.learning.awspring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.model.request.SignedUploadRequest;
import io.awspring.cloud.s3.S3Template;
import java.util.Map;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileInfoControllerIT extends AbstractIntegrationTest {

    @Autowired private S3Template s3Template;

    @Test
    @Order(1)
    void uploadFileToS3() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

        // Perform the request and assert the response
        mockMvc.perform(multipart("/s3/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.fileUrl").value(containsString("/testbucket/test.txt")));
    }

    @Test
    @Order(2)
    void downloadFromS3RouteTest() throws Exception {
        String fileName = "test.txt";

        // Perform the MockMvc request
        mockMvc.perform(get("/s3/download/{name}", fileName))
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                        HttpHeaders.CONTENT_DISPOSITION,
                                        "attachment; filename=\"" + fileName + "\""))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE));
    }

    @Test
    @Order(3)
    void downloadFromS3RouteTestFailed() throws Exception {
        String fileName = "test.pdf";

        // Perform the MockMvc request
        mockMvc.perform(get("/s3/download/{name}", fileName))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                        jsonPath("$.type")
                                .value("https://api.s3project.example.com/errors/file-not-found"))
                .andExpect(jsonPath("$.title").value("File Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("test.pdf"))
                .andExpect(jsonPath("$.instance").value("/s3/download/test.pdf"));
    }

    @Test
    @Order(4)
    void viewAllFromS3Route() throws Exception {

        // Perform the request and assert the response
        mockMvc.perform(get("/s3/view-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("junit.txt"))
                .andExpect(jsonPath("$[1]").value("test.txt"));
    }

    @Test
    @Order(5)
    void viewAllFilesFromDb() throws Exception {
        // Perform the request and assert the response
        mockMvc.perform(get("/s3/view-all-db"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fileName").value("test.txt"))
                .andExpect(jsonPath("$[0].fileUrl").value(containsString("/testbucket/test.txt")));
    }

    @Test
    @Order(6)
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
    @Order(7)
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
