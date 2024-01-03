package com.learning.awspring.service;

import com.learning.awspring.config.ApplicationProperties;
import com.learning.awspring.config.logging.Loggable;
import com.learning.awspring.domain.FileInfo;
import com.learning.awspring.exception.BucketNotFoundException;
import com.learning.awspring.model.GenericResponse;
import com.learning.awspring.model.SignedURLResponse;
import com.learning.awspring.model.SignedUploadRequest;
import com.learning.awspring.repository.FileInfoRepository;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.ObjectMetadata.Builder;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@Slf4j
@RequiredArgsConstructor
@Loggable
public class AwsS3Service {

    private final S3Template s3Template;
    private final ApplicationProperties applicationProperties;
    private final FileInfoRepository fileInfoRepository;
    private final S3Client s3Client;
    private final RestTemplate restTemplate;

    public S3Resource downloadFileFromS3Bucket(
            final String fileName, HttpServletResponse httpServletResponse) throws IOException {
        log.info(
                "Downloading file '{}' from bucket: '{}' ",
                fileName,
                applicationProperties.getBucketName());
        if (this.s3Template.objectExists(applicationProperties.getBucketName(), fileName)) {
            return this.s3Template.download(applicationProperties.getBucketName(), fileName);
        } else {
            throw new FileNotFoundException(fileName);
        }
    }

    public List<String> listObjects() {
        if (getBucketExists()) {
            log.info(
                    "Retrieving object summaries for bucket '{}'",
                    applicationProperties.getBucketName());
            ListObjectsV2Response response =
                    this.s3Client.listObjectsV2(
                            ListObjectsV2Request.builder()
                                    .bucket(applicationProperties.getBucketName())
                                    .build());
            return response.contents().stream().map(S3Object::key).toList();
        } else {
            throw new BucketNotFoundException(applicationProperties.getBucketName());
        }
    }

    public FileInfo uploadObjectToS3(MultipartFile multipartFile)
            throws SdkClientException, IOException {
        if (!getBucketExists()) {
            String location = createBucket(applicationProperties.getBucketName());
            log.info("Created bucket at {}", location);
        }
        String fileName = multipartFile.getOriginalFilename();
        Assert.notNull(fileName, () -> "FileName Can't be null");
        log.info(
                "Uploading file '{}' to bucket: '{}' ",
                fileName,
                applicationProperties.getBucketName());
        S3Resource s3Resource =
                this.s3Template.upload(
                        applicationProperties.getBucketName(),
                        fileName,
                        multipartFile.getInputStream(),
                        ObjectMetadata.builder()
                                .contentType(multipartFile.getContentType())
                                .build());
        var fileInfo = new FileInfo(fileName, s3Resource.getURL().toString(), s3Resource.exists());
        return fileInfoRepository.save(fileInfo);
    }

    public SignedURLResponse downloadFileUsingSignedURL(String bucketName, String fileName) {
        return new SignedURLResponse(
                s3Template.createSignedGetURL(bucketName, fileName, Duration.ofMinutes(1)));
    }

    public GenericResponse uploadFileWithPreSignedUrl(
            MultipartFile multipartFile, SignedUploadRequest signedUploadRequest)
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
                        Duration.ofMinutes(1),
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

    private boolean getBucketExists() {
        return this.s3Template.bucketExists(applicationProperties.getBucketName());
    }

    private String createBucket(String bucketName) {
        return s3Template.createBucket(bucketName);
    }
}
