package com.learning.awspring.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.learning.awspring.config.AwsS3Config;
import com.learning.awspring.domain.FileInfo;
import com.learning.awspring.repository.FileInfoRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3 amazonS3;
    private final AwsS3Config awsS3Config;
    private final FileInfoRepository fileInfoRepository;
    
      public S3ObjectInputStream downloadFileFromS3Bucket(final String fileName) throws FileNotFoundException {
        log.info("Downloading file '{}' from bucket: '{}' ", fileName, awsS3Config.getBucketName());
        if(this.fileInfoRepository.existsByFileName(fileName)) {
            final S3Object s3Object = amazonS3.getObject(awsS3Config.getBucketName(), fileName);
            return s3Object.getObjectContent();
        } else {
            throw new FileNotFoundException(fileName);
        }
      }
    
      public List<S3ObjectSummary> listObjects() {
        log.info("Retrieving object summaries for bucket '{}'", awsS3Config.getBucketName());
        ObjectListing objectListing = amazonS3.listObjects(awsS3Config.getBucketName());
        return objectListing.getObjectSummaries();
      }

    public FileInfo uploadObjectToS3(MultipartFile multipartFile) throws AmazonServiceException, SdkClientException, IOException {
        String fileName = multipartFile.getOriginalFilename();
        log.info("Uploading file '{}' to bucket: '{}' ", fileName, awsS3Config.getBucketName());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getResource().contentLength());
        String fileUrl =
            awsS3Config.getEndpointUrl() + "/" + awsS3Config.getBucketName() + "/" + fileName;
        PutObjectResult putObjectResult =
            amazonS3.putObject(
                awsS3Config.getBucketName(), fileName, multipartFile.getInputStream(), objectMetadata);
        var fileInfo = new FileInfo(fileName, fileUrl, Objects.nonNull(putObjectResult));
        return fileInfoRepository.save(fileInfo);
    }
    
}
