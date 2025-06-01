package com.learning.awspring.service;

import com.learning.awspring.config.ApplicationProperties;
import com.learning.awspring.config.logging.Loggable;
import com.learning.awspring.entities.FileInfo;
import com.learning.awspring.exception.BucketNotFoundException;
import com.learning.awspring.model.request.ObjectTaggingRequest;
import com.learning.awspring.model.request.SignedUploadRequest;
import com.learning.awspring.model.response.GenericResponse;
import com.learning.awspring.model.response.ObjectTaggingResponse;
import com.learning.awspring.model.response.SignedURLResponse;
import com.learning.awspring.repository.FileInfoRepository;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.ObjectMetadata.Builder;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

@Service
@Loggable
public class AwsS3Service {

    private static final Logger log = LoggerFactory.getLogger(AwsS3Service.class);

    private final ApplicationProperties applicationProperties;
    private final FileInfoRepository fileInfoRepository;
    private final S3Template s3Template;
    private final RestTemplate restTemplate;
    private final S3Client s3Client;

    public AwsS3Service(
            ApplicationProperties applicationProperties,
            FileInfoRepository fileInfoRepository,
            S3Template s3Template,
            RestTemplate restTemplate,
            S3Client s3Client) {
        this.applicationProperties = applicationProperties;
        this.fileInfoRepository = fileInfoRepository;
        this.s3Template = s3Template;
        this.restTemplate = restTemplate;
        this.s3Client = s3Client;
    }

    public S3Resource downloadFileFromS3Bucket(final String fileName) throws IOException {
        log.info(
                "Downloading file '{}' from bucket: '{}' ",
                fileName,
                applicationProperties.bucketName());
        if (this.s3Template.objectExists(applicationProperties.bucketName(), fileName)) {
            return this.s3Template.download(applicationProperties.bucketName(), fileName);
        } else {
            throw new FileNotFoundException(fileName);
        }
    }

    public List<String> listObjects() {
        if (getBucketExists()) {
            log.info(
                    "Retrieving object summaries for bucket '{}'",
                    applicationProperties.bucketName());
            return this.s3Template.listObjects(applicationProperties.bucketName(), "").stream()
                    .map(s3Resource -> s3Resource.getLocation().getObject())
                    .toList();
        } else {
            throw new BucketNotFoundException(applicationProperties.bucketName());
        }
    }

    public FileInfo uploadObjectToS3(MultipartFile multipartFile)
            throws SdkClientException, IOException {
        if (!getBucketExists()) {
            String location = createBucket(applicationProperties.bucketName());
            log.info("Created bucket at {}", location);
        }
        String fileName = multipartFile.getOriginalFilename();
        Assert.notNull(fileName, () -> "FileName Can't be null");
        log.info(
                "Uploading file '{}' to bucket: '{}' ",
                fileName,
                applicationProperties.bucketName());
        S3Resource s3Resource =
                this.s3Template.upload(
                        applicationProperties.bucketName(),
                        fileName,
                        multipartFile.getInputStream(),
                        ObjectMetadata.builder()
                                .contentType(multipartFile.getContentType())
                                .build());
        var fileInfo = new FileInfo(fileName, s3Resource.getURL().toString(), s3Resource.exists());
        return fileInfoRepository.save(fileInfo);
    }

    public SignedURLResponse downloadFileUsingSignedURL(
            String bucketName, String fileName, Duration duration) {
        return new SignedURLResponse(s3Template.createSignedGetURL(bucketName, fileName, duration));
    }

    // Overload for backward compatibility
    public SignedURLResponse downloadFileUsingSignedURL(String bucketName, String fileName) {
        return downloadFileUsingSignedURL(bucketName, fileName, Duration.ofMinutes(1));
    }

    public GenericResponse uploadFileWithPreSignedUrl(
            MultipartFile multipartFile, SignedUploadRequest signedUploadRequest, Duration duration)
            throws IOException, URISyntaxException {
        // Step 1: Get UploadFile SignedURL
        Builder objectMetadataBuilder =
                ObjectMetadata.builder().contentType(multipartFile.getContentType());
        signedUploadRequest.metadata().forEach(objectMetadataBuilder::metadata);
        ObjectMetadata metadata = objectMetadataBuilder.build();

        URL preSignedUrl =
                s3Template.createSignedPutURL(
                        signedUploadRequest.bucketName(),
                        Objects.requireNonNull(multipartFile.getOriginalFilename()),
                        duration,
                        metadata,
                        multipartFile.getContentType());
        // Step 2: Upload the file using the pre-signed URL
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.set(HttpHeaders.CONTENT_TYPE, multipartFile.getContentType());
        if (!signedUploadRequest.metadata().isEmpty()) {
            signedUploadRequest.metadata().forEach((k, v) -> fileHeaders.set("x-amz-meta-" + k, v));
        }
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(multipartFile.getBytes(), fileHeaders);

        ResponseEntity<String> fileResponse =
                restTemplate.exchange(
                        preSignedUrl.toURI(), HttpMethod.PUT, fileEntity, String.class);

        if (fileResponse.getStatusCode().is2xxSuccessful()) {
            List<String> amzRequestID = fileResponse.getHeaders().get("x-amz-request-id");
            return new GenericResponse("File uploaded successfully!" + amzRequestID);
        } else {
            return new GenericResponse("File upload failed!");
        }
    }

    // Overload for backward compatibility
    public GenericResponse uploadFileWithPreSignedUrl(
            MultipartFile multipartFile, SignedUploadRequest signedUploadRequest)
            throws IOException, URISyntaxException {
        return uploadFileWithPreSignedUrl(
                multipartFile, signedUploadRequest, Duration.ofMinutes(1));
    }

    // Add or update tags for an object in S3
    public ObjectTaggingResponse tagObject(ObjectTaggingRequest taggingRequest) {
        String fileName = taggingRequest.fileName();
        Map<String, String> tags = taggingRequest.tags();

        try {
            checkIfFileExists(fileName);

            // Convert map to AWS SDK tagging
            Tagging tagging =
                    Tagging.builder()
                            .tagSet(
                                    tags.entrySet().stream()
                                            .map(
                                                    entry ->
                                                            Tag.builder()
                                                                    .key(entry.getKey())
                                                                    .value(entry.getValue())
                                                                    .build())
                                            .toList())
                            .build();

            // Put the object tagging
            PutObjectTaggingResponse putObjectTaggingResponse =
                    s3Client.putObjectTagging(
                            request ->
                                    request.bucket(applicationProperties.bucketName())
                                            .key(fileName)
                                            .tagging(tagging));

            log.info(
                    "Added tags to object {} in bucket {}",
                    fileName,
                    applicationProperties.bucketName());
            return new ObjectTaggingResponse(
                    fileName, tags, putObjectTaggingResponse.sdkHttpResponse().isSuccessful());

        } catch (Exception e) {
            log.error("Error adding tags to object: {}", e.getMessage(), e);
            return new ObjectTaggingResponse(fileName, tags, false);
        }
    }

    // Get tags for an object in S3
    public ObjectTaggingResponse getObjectTags(String fileName) {
        try {
            checkIfFileExists(fileName);

            // Get the object tagging
            var taggingResponse =
                    s3Client.getObjectTagging(
                            request ->
                                    request.bucket(applicationProperties.bucketName())
                                            .key(fileName));

            // Convert AWS SDK tags to a map
            Map<String, String> tags =
                    taggingResponse.tagSet().stream()
                            .collect(Collectors.toMap(Tag::key, Tag::value));

            return new ObjectTaggingResponse(fileName, tags, true);

        } catch (Exception e) {
            log.error("Error getting tags for object: {}", e.getMessage(), e);
            return new ObjectTaggingResponse(fileName, Map.of(), false);
        }
    }

    private void checkIfFileExists(String fileName) throws FileNotFoundException {
        if (!this.s3Template.objectExists(applicationProperties.bucketName(), fileName)) {
            log.error(
                    "File not found in bucket: {} - {}",
                    applicationProperties.bucketName(),
                    fileName);
            throw new FileNotFoundException(fileName);
        }
    }

    private boolean getBucketExists() {
        return this.s3Template.bucketExists(applicationProperties.bucketName());
    }

    private String createBucket(String bucketName) {
        return s3Template.createBucket(bucketName);
    }
}
