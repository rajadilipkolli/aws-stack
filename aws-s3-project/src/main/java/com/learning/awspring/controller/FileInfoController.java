package com.learning.awspring.controller;

import com.learning.awspring.domain.FileInfo;
import com.learning.awspring.model.SignedURLResponse;
import com.learning.awspring.model.SignedUploadRequest;
import com.learning.awspring.service.AwsS3Service;
import com.learning.awspring.service.FileInfoService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileInfoController {

    private final AwsS3Service awsS3Service;
    private final FileInfoService fileInfoService;

    @PostMapping(
            value = "/s3/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    FileInfo uploadFileToS3(@RequestPart(name = "file") MultipartFile multipartFile)
            throws IOException {
        return awsS3Service.uploadObjectToS3(multipartFile);
    }

    @PostMapping("/s3/upload/signed/")
    SignedURLResponse uploadFileUsingSignedURL(
            @RequestBody SignedUploadRequest signedUploadRequest) {
        return awsS3Service.uploadFileUsingSignedURL(signedUploadRequest);
    }

    @GetMapping(value = "/s3/download/{name}")
    ResponseEntity<byte[]> downloadFromS3Route(
            @PathVariable(name = "name") String fileName, HttpServletResponse httpServletResponse)
            throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        var inputStreamResource =
                awsS3Service.downloadFileFromS3Bucket(fileName, httpServletResponse);
        httpHeaders.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/s3/download/signed/{bucketName}/{name}")
    SignedURLResponse downloadFileUsingSignedURL(
            @PathVariable String bucketName, @PathVariable("name") String fileName) {
        return awsS3Service.downloadFileUsingSignedURL(bucketName, fileName);
    }

    @GetMapping("/s3/view-all")
    List<String> viewAllFromS3Route() {
        return awsS3Service.listObjects();
    }

    @GetMapping("/s3/view-all-db")
    List<FileInfo> viewAllFilesFromDb() {
        return fileInfoService.findAllFiles();
    }
}
