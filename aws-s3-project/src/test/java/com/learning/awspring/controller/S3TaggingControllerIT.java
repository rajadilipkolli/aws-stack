package com.learning.awspring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.model.request.ObjectTaggingRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class S3TaggingControllerIT extends AbstractIntegrationTest {

    @Test
    void tagObject_WithValidRequest_ShouldReturnSuccessResponse() throws Exception {
        // Arrange
        String fileName = "test-document.txt";

        MockMultipartFile file =
                new MockMultipartFile(
                        "file", fileName, MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

        // Perform the request and assert the response
        mockMvc.perform(multipart("/s3/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test-document.txt"))
                .andExpect(
                        jsonPath("$.fileUrl")
                                .value(containsString("/testbucket/test-document.txt")));

        Map<String, String> tags = new HashMap<>();
        tags.put("category", "document");
        tags.put("confidential", "yes");
        tags.put("department", "finance");

        ObjectTaggingRequest request = new ObjectTaggingRequest(fileName, tags);

        // Act & Assert
        mockMvc.perform(
                        post("/s3/tags")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.tags.category").value("document"))
                .andExpect(jsonPath("$.tags.confidential").value("yes"))
                .andExpect(jsonPath("$.tags.department").value("finance"))
                .andExpect(jsonPath("$.success").value(true));

        // Then test retrieving the tags
        mockMvc.perform(get("/s3/tags/" + fileName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.tags.category").value("document"))
                .andExpect(jsonPath("$.tags.confidential").value("yes"))
                .andExpect(jsonPath("$.tags.department").value("finance"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getObjectTags_WithNonexistentFile_ShouldReturnFailureResponse() throws Exception {
        // Arrange
        String fileName = "nonexistent-file.pdf";

        // Act & Assert
        mockMvc.perform(get("/s3/tags/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.tags").isEmpty())
                .andExpect(jsonPath("$.success").value(false));
    }
}
