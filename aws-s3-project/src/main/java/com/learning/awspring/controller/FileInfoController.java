package com.learning.awspring.controller;

import java.io.IOException;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.learning.awspring.domain.FileInfo;
import com.learning.awspring.service.AwsS3Service;
import com.learning.awspring.service.FileInfoService;

import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/s3/upload")
    FileInfo uploadFileToS3(@RequestPart(name = "file") MultipartFile multipartFile) throws AmazonServiceException, SdkClientException, IOException{
        return awsS3Service.uploadObjectToS3(multipartFile);
    }

    @GetMapping("/s3/view-all-db")
    List<FileInfo> viewAllFilesFromDb(){
        return fileInfoService.findAllFiles();
    }

}
