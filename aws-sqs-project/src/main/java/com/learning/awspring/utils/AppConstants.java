package com.learning.awspring.utils;

public final class AppConstants {
    public static final String PROFILE_LOCAL = "local";
    public static final String PROFILE_PROD = "prod";
    public static final String PROFILE_NOT_PROD = "!" + PROFILE_PROD;
    public static final String PROFILE_TEST = "test";
    public static final String PROFILE_IT = "integration-test";
    // Name of the queue. Developers are free to choose their queue name. (
    // "http://localhost:4566/000000000000/spring-boot-amazon-sqs")
    public static final String QUEUE = "spring-boot-amazon-sqs";
}
