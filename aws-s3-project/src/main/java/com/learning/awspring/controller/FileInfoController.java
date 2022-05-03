package com.learning.awspring.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.learning.awspring.domain.FileInfo;
import com.learning.awspring.service.AwsS3Service;
import com.learning.awspring.service.FileInfoService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileInfoController {

    private final AwsS3Service awsS3Service;
    private final FileInfoService fileInfoService;

    @PostMapping(value = "/s3/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    FileInfo uploadFileToS3(@RequestPart(name = "file") MultipartFile multipartFile)
            throws AmazonServiceException, SdkClientException, IOException {
        return awsS3Service.uploadObjectToS3(multipartFile);
    }

    @GetMapping("/s3/download/{name}")
    S3ObjectInputStream downloadFromS3Route(@PathVariable(name = "name") String fileName) throws FileNotFoundException {
        return awsS3Service.downloadFileFromS3Bucket(fileName);
    }

    @GetMapping("/s3/view-all")
    List<S3ObjectSummary> viewAllFromS3Route() {
        return awsS3Service.listObjects();
    }

    @GetMapping("/s3/view-all-db")
    List<FileInfo> viewAllFilesFromDb() {
        return fileInfoService.findAllFiles();
    }

}
