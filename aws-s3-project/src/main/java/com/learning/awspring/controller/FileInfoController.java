package com.learning.awspring.controller;

import com.learning.awspring.domain.FileInfo;
import com.learning.awspring.model.GenericResponse;
import com.learning.awspring.model.SignedURLResponse;
import com.learning.awspring.model.SignedUploadRequest;
import com.learning.awspring.service.AwsS3Service;
import com.learning.awspring.service.FileInfoService;
import io.awspring.cloud.s3.S3Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping(
            value = "/s3/upload/signed/",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    GenericResponse uploadFileWithPreSignedUrl(
            @RequestPart("json") SignedUploadRequest signedUploadRequest,
            @RequestPart("file") MultipartFile multipartFile)
            throws IOException, URISyntaxException {
        return awsS3Service.uploadFileWithPreSignedUrl(multipartFile, signedUploadRequest);
    }

    @GetMapping(value = "/s3/download/{name}")
    ResponseEntity<InputStreamResource> downloadFromS3Route(
            @PathVariable(name = "name") String fileName, HttpServletResponse httpServletResponse)
            throws IOException {
        S3Resource s3Resource =
                awsS3Service.downloadFileFromS3Bucket(fileName, httpServletResponse);

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + s3Resource.getFilename());
        headers.add(HttpHeaders.CONTENT_TYPE, s3Resource.contentType());
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(s3Resource.contentLength()));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(s3Resource.contentType()))
                .body(new InputStreamResource(s3Resource.getInputStream()));
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
