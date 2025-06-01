package com.learning.awspring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.learning.awspring.model.request.ObjectTaggingRequest;
import com.learning.awspring.model.response.ObjectTaggingResponse;
import com.learning.awspring.service.AwsS3Service;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class S3TaggingControllerTest {

    @Mock private AwsS3Service awsS3Service;

    @InjectMocks private S3TaggingController controller;

    @Test
    void tagObject_shouldReturnSuccessResponse() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("category", "document");
        tags.put("confidential", "yes");

        ObjectTaggingRequest request = new ObjectTaggingRequest("test-file.pdf", tags);
        ObjectTaggingResponse mockResponse = new ObjectTaggingResponse("test-file.pdf", tags, true);

        when(awsS3Service.tagObject(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ObjectTaggingResponse> response = controller.tagObject(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        assertEquals(true, response.getBody().success());
        assertEquals("test-file.pdf", response.getBody().fileName());
        assertEquals(tags, response.getBody().tags());
    }

    @Test
    void getObjectTags_shouldReturnTags() {
        // Arrange
        String fileName = "test-file.pdf";
        Map<String, String> tags = new HashMap<>();
        tags.put("category", "document");
        tags.put("confidential", "yes");

        ObjectTaggingResponse mockResponse = new ObjectTaggingResponse(fileName, tags, true);

        when(awsS3Service.getObjectTags(fileName)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ObjectTaggingResponse> response = controller.getObjectTags(fileName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        assertEquals(true, response.getBody().success());
        assertEquals(fileName, response.getBody().fileName());
        assertEquals(tags, response.getBody().tags());
    }
}
