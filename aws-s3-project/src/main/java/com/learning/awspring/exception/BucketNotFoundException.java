package com.learning.awspring.exception;

public class BucketNotFoundException extends RuntimeException {

    public BucketNotFoundException(String bucketName) {
        super("Bucket with name %s doesNotExists".formatted(bucketName));
    }
}
