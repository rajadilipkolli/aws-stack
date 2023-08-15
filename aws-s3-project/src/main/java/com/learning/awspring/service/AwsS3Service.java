package com.learning.awspring.service;

import com.learning.awspring.config.ApplicationProperties;
import com.learning.awspring.domain.FileInfo;
import com.learning.awspring.model.SignedURLResponse;
import com.learning.awspring.repository.FileInfoRepository;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsS3Service {

    private final S3Template s3Template;
    private final ApplicationProperties applicationProperties;
    private final FileInfoRepository fileInfoRepository;
    private final S3Client s3Client;

    public byte[] downloadFileFromS3Bucket(
            final String fileName, HttpServletResponse httpServletResponse) throws IOException {
        log.info(
                "Downloading file '{}' from bucket: '{}' ",
                fileName,
                applicationProperties.getBucketName());
        if (this.fileInfoRepository.existsByFileName(fileName)) {
            S3Resource s3Resource =
                    this.s3Template.download(applicationProperties.getBucketName(), fileName);
            httpServletResponse.setContentType(s3Resource.contentType());
            InputStream inputStream = s3Resource.getInputStream();
            return IOUtils.toByteArray(inputStream, s3Resource.contentLength());
        } else {
            throw new FileNotFoundException(fileName);
        }
    }

    public List<String> listObjects() {
        log.info(
                "Retrieving object summaries for bucket '{}'",
                applicationProperties.getBucketName());
        ListObjectsV2Response response =
                this.s3Client.listObjectsV2(
                        ListObjectsV2Request.builder()
                                .bucket(applicationProperties.getBucketName())
                                .build());
        return response.contents().stream().map(S3Object::key).toList();
    }

    public FileInfo uploadObjectToS3(MultipartFile multipartFile)
            throws SdkClientException, IOException {
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
                s3Template
                        .createSignedGetURL(bucketName, fileName, Duration.ofMinutes(1))
                        .toString());
    }
}
