package com.learning.awspring.exception;

public class BucketNotFoundException extends RuntimeException {

    public BucketNotFoundException(String bucketName) {
        super(String.format("Bucket with name %s doesNotExists", bucketName));
    }
}
