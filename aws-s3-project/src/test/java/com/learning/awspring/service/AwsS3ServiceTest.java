package com.learning.awspring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.learning.awspring.config.ApplicationProperties;
import com.learning.awspring.entities.FileInfo;
import com.learning.awspring.exception.BucketNotFoundException;
import com.learning.awspring.model.request.ObjectTaggingRequest;
import com.learning.awspring.model.response.ObjectTaggingResponse;
import com.learning.awspring.model.response.SignedURLResponse;
import com.learning.awspring.repository.FileInfoRepository;
import io.awspring.cloud.s3.Location;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.Tag;

@ExtendWith(MockitoExtension.class)
class AwsS3ServiceTest {

    @Mock private ApplicationProperties applicationProperties;

    @Mock private FileInfoRepository fileInfoRepository;

    @Mock private S3Template s3Template;

    @Mock private RestTemplate restTemplate;

    @Mock private S3Client s3Client;

    @Mock private S3Resource s3Resource;

    @Mock private MultipartFile multipartFile;

    private AwsS3Service awsS3Service;

    @BeforeEach
    void setUp() {
        awsS3Service =
                new AwsS3Service(
                        applicationProperties,
                        fileInfoRepository,
                        s3Template,
                        restTemplate,
                        s3Client);
    }

    @Test
    void downloadFileFromS3Bucket_FileExists_Success() throws IOException {
        // Arrange
        String fileName = "testFile.txt";
        String bucketName = "testBucket";
        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(s3Template.objectExists(bucketName, fileName)).thenReturn(true);
        when(s3Template.download(bucketName, fileName)).thenReturn(s3Resource);

        // Act
        S3Resource result = awsS3Service.downloadFileFromS3Bucket(fileName);

        // Assert
        assertNotNull(result);
        assertEquals(s3Resource, result);
        verify(s3Template).objectExists(bucketName, fileName);
        verify(s3Template).download(bucketName, fileName);
    }

    @Test
    void downloadFileFromS3Bucket_FileDoesNotExist_ThrowsFileNotFoundException() {
        // Arrange
        String fileName = "nonExistentFile.txt";
        String bucketName = "testBucket";
        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(s3Template.objectExists(bucketName, fileName)).thenReturn(false);

        // Act & Assert
        assertThrows(
                FileNotFoundException.class, () -> awsS3Service.downloadFileFromS3Bucket(fileName));
        verify(s3Template).objectExists(bucketName, fileName);
        verify(s3Template, never()).download(anyString(), anyString());
    }

    @Test
    void listObjects_BucketExists_Success() {
        // Arrange
        String bucketName = "testBucket";
        List<S3Resource> s3Resources = new ArrayList<>();
        S3Resource resource1 = mock(S3Resource.class);
        S3Resource resource2 = mock(S3Resource.class);
        s3Resources.add(resource1);
        s3Resources.add(resource2);

        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(s3Template.bucketExists(bucketName)).thenReturn(true);
        when(s3Template.listObjects(eq(bucketName), anyString())).thenReturn(s3Resources);
        when(resource1.getLocation()).thenReturn(mock(Location.class));
        when(resource2.getLocation()).thenReturn(mock(Location.class));
        when(resource1.getLocation().getObject()).thenReturn("file1.txt");
        when(resource2.getLocation().getObject()).thenReturn("file2.txt");

        // Act
        List<String> result = awsS3Service.listObjects();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1.txt", result.get(0));
        assertEquals("file2.txt", result.get(1));
        verify(s3Template).bucketExists(bucketName);
        verify(s3Template).listObjects(bucketName, "");
    }

    @Test
    void listObjects_BucketDoesNotExist_ThrowsBucketNotFoundException() {
        // Arrange
        String bucketName = "nonExistentBucket";
        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(s3Template.bucketExists(bucketName)).thenReturn(false);

        // Act & Assert
        assertThrows(BucketNotFoundException.class, () -> awsS3Service.listObjects());
        verify(s3Template).bucketExists(bucketName);
        verify(s3Template, never()).listObjects(anyString(), anyString());
    }

    @Test
    void uploadObjectToS3_Success() throws IOException {
        // Arrange
        String bucketName = "testBucket";
        String fileName = "testFile.txt";
        String contentType = "text/plain";
        long fileSize = 1024L;
        URL url = new URL("https://example.com/testbucket/testfile.txt");

        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(s3Template.bucketExists(bucketName)).thenReturn(true);
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getInputStream())
                .thenReturn(new ByteArrayInputStream("test content".getBytes()));
        when(s3Template.upload(
                        eq(bucketName),
                        eq(fileName),
                        any(InputStream.class),
                        any(ObjectMetadata.class)))
                .thenReturn(s3Resource);
        when(s3Resource.getURL()).thenReturn(url);
        when(s3Resource.exists()).thenReturn(true);
        when(fileInfoRepository.save(any(FileInfo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        FileInfo result = awsS3Service.uploadObjectToS3(multipartFile);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
        assertEquals(url.toString(), result.getFileUrl());
        assertEquals(fileSize, result.getFileSize());
        assertEquals(contentType, result.getContentType());
        assertEquals(bucketName, result.getBucketName());
        assertTrue(result.isUploadSuccessFull());

        verify(s3Template).bucketExists(bucketName);
        verify(s3Template)
                .upload(
                        eq(bucketName),
                        eq(fileName),
                        any(InputStream.class),
                        any(ObjectMetadata.class));
        verify(fileInfoRepository).save(any(FileInfo.class));
    }

    @Test
    void downloadFileUsingSignedURL_Success() throws MalformedURLException {
        // Arrange
        String bucketName = "testBucket";
        String fileName = "testFile.txt";
        Duration duration = Duration.ofMinutes(10);
        URL signedUrl = URI.create("https://example.com/signedurl").toURL();

        when(s3Template.createSignedGetURL(bucketName, fileName, duration)).thenReturn(signedUrl);

        // Act
        SignedURLResponse result =
                awsS3Service.downloadFileUsingSignedURL(bucketName, fileName, duration);

        // Assert
        assertNotNull(result);
        assertEquals(signedUrl.toString(), result.url());
        verify(s3Template).createSignedGetURL(bucketName, fileName, duration);
    }

    @Test
    void tagObject_Success() {
        // Arrange
        String bucketName = "testBucket";
        String fileName = "testFile.txt";
        Map<String, String> tags = Map.of("key1", "value1", "key2", "value2");
        ObjectTaggingRequest taggingRequest = new ObjectTaggingRequest(fileName, tags);

        // Mock dependencies
        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(s3Template.objectExists(bucketName, fileName)).thenReturn(true);

        // Create successful HTTP response
        SdkHttpResponse httpResponse =
                SdkHttpResponse.builder().statusCode(200).statusText("OK").build();

        // Create response object that will be returned by S3Client
        PutObjectTaggingResponse putResponse = mock(PutObjectTaggingResponse.class);
        when(putResponse.sdkHttpResponse()).thenReturn(httpResponse);

        // Mock S3Client behavior with proper Consumer handling
        doAnswer(
                        invocation -> {
                            // Get the Consumer parameter
                            Consumer<?> consumer = invocation.getArgument(0);

                            // The consumer is called with a builder in the real implementation
                            // Here we're just verifying it's called and returning our mock response
                            assertNotNull(consumer);

                            return putResponse;
                        })
                .when(s3Client)
                .putObjectTagging(any(Consumer.class));

        // Act
        ObjectTaggingResponse result = awsS3Service.tagObject(taggingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.fileName());
        assertEquals(tags, result.tags());
        assertTrue(result.success());

        // Verify interactions
        verify(applicationProperties, times(2)).bucketName();
        verify(s3Template).objectExists(bucketName, fileName);
        verify(s3Client).putObjectTagging(any(Consumer.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getObjectTags_Success() {
        // Arrange
        String bucketName = "testBucket";
        String fileName = "testFile.txt";
        List<Tag> tagList =
                List.of(
                        Tag.builder().key("key1").value("value1").build(),
                        Tag.builder().key("key2").value("value2").build());

        when(applicationProperties.bucketName()).thenReturn(bucketName);
        when(s3Template.objectExists(bucketName, fileName)).thenReturn(true);

        GetObjectTaggingResponse getObjectTaggingResponse =
                GetObjectTaggingResponse.builder().tagSet(tagList).build();

        when(s3Client.getObjectTagging(any(Consumer.class))).thenReturn(getObjectTaggingResponse);

        // Act
        ObjectTaggingResponse result = awsS3Service.getObjectTags(fileName);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.fileName());
        assertEquals(2, result.tags().size());
        assertEquals("value1", result.tags().get("key1"));
        assertEquals("value2", result.tags().get("key2"));
        assertTrue(result.success());

        verify(s3Template).objectExists(bucketName, fileName);
        verify(s3Client, times(1)).getObjectTagging(any(Consumer.class));
    }
}
