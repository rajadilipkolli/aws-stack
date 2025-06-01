package com.learning.awspring.controller;

import com.learning.awspring.model.request.ObjectTaggingRequest;
import com.learning.awspring.model.response.ObjectTaggingResponse;
import com.learning.awspring.service.AwsS3Service;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3/tags")
public class S3TaggingController {

    private final AwsS3Service awsS3Service;

    public S3TaggingController(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    /**
     * Add or update tags for an object in S3.
     *
     * @param taggingRequest The request containing the object key and tags
     * @return Response with the result of the tagging operation
     */
    @PostMapping
    public ResponseEntity<ObjectTaggingResponse> tagObject(
            @RequestBody @Valid ObjectTaggingRequest taggingRequest) {
        ObjectTaggingResponse response = awsS3Service.tagObject(taggingRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all tags for an object in S3.
     *
     * @param fileName The name of the file/object
     * @return Response with the object's tags
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<ObjectTaggingResponse> getObjectTags(
            @PathVariable @Valid @NotBlank String fileName) {
        ObjectTaggingResponse response = awsS3Service.getObjectTags(fileName);
        return ResponseEntity.ok(response);
    }
}
